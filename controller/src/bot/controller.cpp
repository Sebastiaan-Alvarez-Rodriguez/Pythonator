#include "bot/controller.hpp"
#include "error/exceptions.hpp"
#include "error/logging.hpp"

#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <termios.h>
#include <cstring>
#include <sstream>

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

    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;

    tty.c_iflag &= ~IGNBRK;
    tty.c_lflag = 0;

    tty.c_oflag = 0;
    tty.c_cc[VMIN]  = 1;
    tty.c_cc[VTIME] = 5;

    tty.c_iflag &= ~(IXON | IXOFF | IXANY);
    tty.c_cflag |= (CLOCAL | CREAD);
    tty.c_cflag &= ~(PARENB | PARODD);
    tty.c_cflag &= ~CSTOPB;
    tty.c_cflag &= ~CRTSCTS;
    
    if(tcsetattr(this->usb_port, TCSANOW, &tty) != 0) {
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
    if(result < 0 || (size_t)result != command_size) {
        log_error("Bot IO error during write, result: %lld", (long long)result);
        return BotStatus::IO_ERROR;
    }

    BotStatus usb_result = BotStatus::SUCCESS;
    result = read(this->usb_port, &usb_result, sizeof(usb_result));
    if(result < 0 || (size_t)result != sizeof(usb_result)) {
        log_error("Bot IO error during read, result: %lld", (long long)result);
        return BotStatus::IO_ERROR;
    }
    
    if(usb_result != BotStatus::SUCCESS) {
        log_error("Bot returned status %d", (int)usb_result);
        
        std::stringstream ss;
        command->print(ss);
        std::string str = ss.str();
        log_error("Command: %s", str.c_str());
    }
    return usb_result;
}
