#include "bot/command.hpp"

#include <cstring>
#include <iostream>

const char* COMMAND_NAMES[] = {
    "START",
    "END",
    "PEN DOWN",
    "PEN UP",
    "ORIG",
    "LINE"
};

ZeroArgBotCommand::ZeroArgBotCommand(BotCommandType type) : type(type) {}

size_t ZeroArgBotCommand::size() const {
    return 1;
}

void ZeroArgBotCommand::write(void* output) const {
    std::memcpy(output, &this->type, sizeof(uint8_t));
}

void ZeroArgBotCommand::print(std::ostream& output) const {
    output << COMMAND_NAMES[(size_t)this->type];
}

CoordinateBotCommand::CoordinateBotCommand(BotCommandType type, Coord data) : type(type), arg(data) {}

size_t CoordinateBotCommand::size() const {
    return 5;
}

void CoordinateBotCommand::write(void* output) const {
    std::memcpy(output, &this->type, sizeof(uint8_t));
    std::memcpy((uint8_t*)output + 1, &this->arg, sizeof(uint16_t) * 2);
}

void CoordinateBotCommand::print(std::ostream& output) const {
    output << COMMAND_NAMES[(size_t)this->type] << " (" << this->arg.x << "," << this->arg.y << ")";
}
