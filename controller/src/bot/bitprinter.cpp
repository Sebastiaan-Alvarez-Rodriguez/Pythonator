#include "bot/bitprinter.hpp"

#include <iostream>

BotBitPrinter::BotBitPrinter(std::ostream& output) : output(output) {}

BotStatus BotBitPrinter::printCommand(const std::unique_ptr<BotCommand>& command) {
    size_t command_size = command->size();
    uint8_t* command_buffer = new uint8_t[command_size];
    command->write(command_buffer);

    this->output.write((const char*)command_buffer, command_size);
    this->output.flush();

    delete[] command_buffer;
    return BotStatus::SUCCESS;
}
