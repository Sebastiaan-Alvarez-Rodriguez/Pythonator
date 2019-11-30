#ifndef _BOT_DEVICE_HPP_
#define _BOT_DEVICE_HPP_

#include <cstdint>
#include <vector>
#include <memory>

#include "bot/command.hpp"
#include "image/coordinate.hpp"

enum class BotStatus : uint8_t {
    SUCCESS,
    OUT_OF_BOUNDS,
    UNKNOWN_COMMAND,
    IO_ERROR
};

class BotDevice {
    private:
        void appendPath(std::vector<std::unique_ptr<BotCommand>>&, const std::vector<std::pair<Coord, Coord>>&);
        BotStatus printCommands(std::vector<std::unique_ptr<BotCommand>>&);
    protected:
        virtual BotStatus printCommand(const std::unique_ptr<BotCommand>&) = 0;
    public:
        virtual ~BotDevice() = default;

        BotStatus writeLines(const std::vector<std::pair<Coord, Coord>>&);
};

#endif // _BOT_DEVICE_HPP_
