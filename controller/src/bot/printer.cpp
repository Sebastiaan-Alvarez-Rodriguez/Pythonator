#include "bot/printer.hpp"

#include <iostream>

BotStatus BotPrinter::printCommand(const std::unique_ptr<BotCommand>& command) {
    command->print(this->output);
    this->output << std::endl;
    return BotStatus::SUCCESS;
}

BotPrinter::BotPrinter(std::ostream& output) : output(output) {}
