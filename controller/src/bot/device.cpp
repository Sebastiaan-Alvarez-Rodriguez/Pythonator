#include "bot/device.hpp"

void BotDevice::appendPath(std::vector<std::unique_ptr<BotCommand>>& commands, const std::vector<std::pair<Coord, Coord>>& line_list) {
    bool drawing = false;

    Coord current_coord = {0,0};

    for(auto& it : line_list) {
        //Check for correct position
        if(!(it.first.x == current_coord.x && it.first.y == current_coord.y)) {
            if(drawing) {
                commands.push_back(std::unique_ptr<BotCommand>(new ZeroArgBotCommand(BotCommandType::PEN_UP)));
                drawing = false;
            }
            commands.push_back(std::unique_ptr<BotCommand>(new CoordinateBotCommand(BotCommandType::LINE, it.first)));
            current_coord = it.first;
        }

        //Check if drawing
        if(!drawing) {
            commands.push_back(std::unique_ptr<BotCommand>(new ZeroArgBotCommand(BotCommandType::PEN_DOWN)));
            drawing = true;
        }

        commands.push_back(std::unique_ptr<BotCommand>(new CoordinateBotCommand(BotCommandType::LINE, it.second)));
        current_coord = it.second;
    }
}

BotStatus BotDevice::writeLines(const std::vector<std::pair<Coord, Coord>>& line_list) {
    std::vector<std::unique_ptr<BotCommand>> commands;

    commands.push_back(std::unique_ptr<BotCommand>(new ZeroArgBotCommand(BotCommandType::START)));

    this->appendPath(commands, line_list);

    commands.push_back(std::unique_ptr<BotCommand>(new ZeroArgBotCommand(BotCommandType::END)));

    return this->printCommands(commands);
}

BotStatus BotDevice::printCommands(std::vector<std::unique_ptr<BotCommand>>& commands) {
    for(auto& it : commands) {
        BotStatus result = this->printCommand(it);
        if(result != BotStatus::SUCCESS) {
            //Reset the bot
            std::unique_ptr<BotCommand> command(new ZeroArgBotCommand(BotCommandType::END));
            this->printCommand(command);
            return result;
        }
    }
    return BotStatus::SUCCESS;
}
