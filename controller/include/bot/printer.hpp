#ifndef _BOT_PRINTER_HPP_
#define _BOT_PRINTER_HPP_

#include <iosfwd>

#include "bot/device.hpp"

class BotPrinter : public BotDevice {
    private:
        std::ostream& output;
    protected:
        BotStatus printCommand(const std::unique_ptr<BotCommand>&);
    public:
        BotPrinter(std::ostream&);
        ~BotPrinter() = default;
};

#endif
