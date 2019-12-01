#include "bot/controller.hpp"
#include "error/exceptions.hpp"

#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <termios.h>
#include <cstring>

BotController::BotController(const BotInfo& bot_device) {
    this->usb_port = open(bot_device.device_name.c_str(), O_RDWR | O_NOCTTY);
    if(this->usb_port == -1)
        throw USBException("Failed to open bot device");

    struct termios tty_old;
    struct termios tty;
    std::memset(&tty_old, 0, sizeof(tty_old));
    std::memset(&tty, 0, sizeof(tty));

    if(tcgetattr(this->usb_port, &tty_old) != 0) {
        close(this->usb_port);
        throw USBException("Failed to get USB attributes");
    }

    tty = tty_old;
    cfsetospeed(&tty, bot_device.baud_rate);
    cfsetispeed(&tty, bot_device.baud_rate);

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

    //Use for raw binary IO
    cfmakeraw(&tty);

    tcflush(this->usb_port, TCIFLUSH);
    if(tcsetattr(this->usb_port, TCSAFLUSH, &tty) != 0) {
        close(this->usb_port);
        throw USBException("Failed to set USB attributes");
    }
}

BotController::~BotController() {
    close(this->usb_port);
}

BotStatus BotController::printCommand(const std::unique_ptr<BotCommand>& command) {
    size_t command_size = command->size();
    uint8_t* command_buffer = new uint8_t[command_size];
    command->write(command_buffer);

    ssize_t result = write(this->usb_port, command_buffer, command_size);
    delete[] command_buffer;
    if(result < 0 || (size_t)result != command_size)
        return BotStatus::IO_ERROR;

    BotStatus usb_result;
    result = read(this->usb_port, &usb_result, sizeof(usb_result));
    if(result < 0 || (size_t)result != sizeof(usb_result))
        return BotStatus::IO_ERROR;
    return usb_result;
}
