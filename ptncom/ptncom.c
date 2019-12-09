#include <unistd.h>
#include <termios.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <stdint.h>
#include <stddef.h>

#define MAX_COMMAND_PARAMS 4
#define ARDUINO_RESET_TIMEOUT 2

// See doc/serial_protocol.md
enum command {
    CMD_START = 0x00,
    CMD_END = 0x01,
    CMD_PEN_DOWN = 0x02,
    CMD_PEN_UP = 0x03,
    CMD_ORIG = 0x04,
    CMD_LINE = 0x05
};

// See doc/serial_protocol.md
enum status {
    STATUS_OK = 0x00,
    STATUS_ERR_BOUNDS = 0x01,
    STATUS_ERR_UNKNOWN_CMD = 0x02
};

bool device_write(int fd, const uint8_t* data, size_t n) {
    ssize_t written;
    do {
        written = write(fd, data, n);
        if (written < 0) {
            return false;
        }

        n -= written;
    } while (n != 0);

    return true;
}

int device_read_u8(int fd) {
    ssize_t r;
    uint8_t byte;
    do {
        r = read(fd, &byte, 1);
        if (r < 0) {
            return -1;
        }
    } while (r != 1);

    return byte;
}

//https://stackoverflow.com/questions/6947413/how-to-open-read-and-write-from-serial-port-in-c
bool set_attribs(int fd, speed_t speed, int parity) {
    struct termios tty;
    memset(&tty, 0, sizeof(struct termios));

    if (tcgetattr(fd, &tty) != 0) {
        return false;
    }

    cfsetispeed(&tty, speed);
    cfsetospeed(&tty, speed);

    //Turn of input processing
    tty.c_iflag &= ~(IGNBRK | BRKINT | ICRNL | INLCR | PARMRK | INPCK | ISTRIP | IXON);
    //Turn of output processing
    tty.c_oflag = 0;
    //Turn of line processing
    tty.c_lflag = ~(ECHO | ECHONL | ICANON | IEXTEN | ISIG);
    //Disable pararity checking, clear char size mask, force 8 bit char size mask
    tty.c_cflag = ~(CSIZE | PARENB);
    tty.c_cflag |= CS8;

    //Read blocks until one byte is received, no timeout between characters
    tty.c_cc[VMIN] = 1;
    tty.c_cc[VTIME] = 0;

    if (tcsetattr(fd, TCSANOW, &tty) != 0) {
        return false;
    }

    return true;
}

int ptn_write(int fd, const uint8_t* bytes, size_t n, bool verbose) {
    if (verbose) {
        printf("Sending [");
        for (int i = 0; i < n; ++i) {
            if (i == 0) {
                printf("%d", bytes[i]);
            } else {
                printf(", %d", bytes[i]);
            }
        }
        puts("]");
    }

    if (!device_write(fd, bytes, n)) {
        puts("Error: failed to write device");
        return -1;
    }

    int byte = device_read_u8(fd);
    if (byte == -1) {
        puts("Error: failed to read device");
        return -1;
    }

    if (verbose) {
        printf("Received %d\n", byte);
    }

    return byte;
}

void print_help(const char* program) {
    printf(
        "Utility to send commands to the Pythonator\n."
        "Usage: %s [-v] [-r] [-h] [-i <input>] <device>\n"
        "By default commands are read from stdin.\n"
        "\n"
        "Parameters:\n"
        "-v          Verbose, print sent and received bytes.\n"
        "-r          Recover after an error by sending CMD_END\n"
        "-h          Show this message and exit\n"
        "-i <input>  Read commands from <input>\n",
        program
    );
}

int main(int argc, const char* argv[]) {
    const char* device = NULL;
    const char* input = NULL;
    bool verbose = false;
    bool recover = false;

    for (int i = 1; i < argc; ++i) {
        if (strcmp(argv[i], "-v") == 0) {
            verbose = true;
        } else if (strcmp(argv[i], "-r") == 0) {
            recover = true;
        } else if (strcmp(argv[i], "-h") == 0) {
            print_help(argv[0]);
            return EXIT_SUCCESS;
        } else if (strcmp(argv[i], "-i") == 0) {
            if (++i >= argc) {
                puts("Error: missing parameter <input> of argument '-i'");
                return EXIT_FAILURE;
            }

            input = argv[i];
        } else {
            device = argv[i];
        }
    }

    if (!device) {
        puts("Error: missing parameter <device>");
        return EXIT_FAILURE;
    }

    int input_fd = 0;
    if (input) {
        input_fd = open(input, O_RDONLY);
        if (input_fd < 0) {
            puts("Error: failed to open input");
            goto end;
        }
    }

    int device_fd = open(device, O_RDWR | O_NOCTTY | O_SYNC);
    if (device_fd < 0) {
        puts("Error: failed to open device");
        goto end;
    }

    if (!set_attribs(device_fd, B115200, 0)) {
        puts("Error: failed to set device attributes");
        goto end;
    }

    sleep(ARDUINO_RESET_TIMEOUT); // wait until the device has restarted

    while (true) {
        uint8_t bytes[MAX_COMMAND_PARAMS + 1];
        size_t params = 0;

        ssize_t r = read(input_fd, bytes, 1);
        if (r < 0) {
            puts("Error: failed to read input");
            goto end;
        } else if (r == 0) {
            goto end;
        }

        if (((enum command) bytes[0]) == CMD_LINE) {
            params = 4;
        }

        r = read(input_fd, &bytes[1], params);
        if (r != params) {
            puts("Error: failed to read input");
            goto end;
        }

        switch (ptn_write(device_fd, bytes, params + 1, verbose)) {
        case STATUS_OK:
            break;
        case STATUS_ERR_BOUNDS:
            puts("Device error: Out of bounds");
            goto error;
        case STATUS_ERR_UNKNOWN_CMD:
            puts("Device error: Unknown command");
        case -1:
            goto error;
        }
    }

error:
    if (recover) {
        uint8_t end = CMD_END;

        if (verbose) {
            printf("Sending [%d]\n", end);
        }

        if (ptn_write(device_fd, &end, 1, verbose) != STATUS_OK) {
            puts("Error: failed to recover");
        }
    }

end:
    close(device_fd);
    if (input_fd != 0) {
        close(input_fd);
    }
    return EXIT_SUCCESS;
}
