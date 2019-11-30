#ifndef _BOT_COMMAND_HPP_
#define _BOT_COMMAND_HPP_

#include "image/coordinate.hpp"

#include <cstdint>
#include <cstddef>
#include <iosfwd>

enum class BotCommandType : uint8_t {
    START,
    END,
    PEN_DOWN,
    PEN_UP,
    ORIG,
    LINE
};

class BotCommand {
    public:
        virtual ~BotCommand() = default;

        virtual size_t size() const = 0;
        virtual void write(void*) const = 0;
        virtual void print(std::ostream&) const = 0;
};

class ZeroArgBotCommand : public BotCommand {
    private:
        BotCommandType type;
    public:
        ZeroArgBotCommand(BotCommandType);
        ~ZeroArgBotCommand() = default;

        virtual size_t size() const;
        virtual void write(void*) const;
        virtual void print(std::ostream&) const;
};

class CoordinateBotCommand : public BotCommand {
    private:
        BotCommandType type;
        Coord arg;
    public:
        CoordinateBotCommand(BotCommandType, Coord);
        ~CoordinateBotCommand() = default;

        virtual size_t size() const;
        virtual void write(void*) const;
        virtual void print(std::ostream&) const;
};

#endif // _BOT_COMMAND_HPP_
