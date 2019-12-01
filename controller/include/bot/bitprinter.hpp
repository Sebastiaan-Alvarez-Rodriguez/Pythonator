#ifndef _BOT_BITPRINTER_HPP_
#define _BOT_BITPRINTER_HPP_

#include <iosfwd>

#include "bot/device.hpp"

class BotBitPrinter : public BotDevice {
    private:
        std::ostream& output;
    protected:
        BotStatus printCommand(const std::unique_ptr<BotCommand>&);
    public:
        BotBitPrinter(std::ostream&);
        ~BotBitPrinter() = default;
};

#endif
