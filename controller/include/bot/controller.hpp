#ifndef _BOT_CONTROLLER_HPP_
#define _BOT_CONTROLLER_HPP_

#include <vector>
#include <memory>
#include <termios.h>

#include "bot/command.hpp"
#include "bot/device.hpp"

struct BotInfo {
    const char* device_name;
    speed_t boud_rate;
};

class BotController : public BotDevice {
    private:
        int usb_port;
    protected:
        virtual BotStatus printCommand(const std::unique_ptr<BotCommand>&);
    public:
        BotController(const BotInfo&);
        ~BotController();
};

#endif // _BOT_CONTROLLER_HPP_
