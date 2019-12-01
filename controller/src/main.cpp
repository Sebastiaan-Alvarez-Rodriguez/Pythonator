#include "bluetooth/server.hpp"
#include "bot/controller.hpp"
#include "bot/printer.hpp"
#include "bot/bitprinter.hpp"
#include "config/parser.hpp"

#include <iostream>
#include <fstream>

struct BotConfigInfo {
    BotDevice* device;
    std::ostream* file_stream;
    size_t canvas_width;
    size_t canvas_height;
};

struct BluetoothConfigInfo {
    size_t channel;
};

speed_t get_baud_rate(size_t baud_rate) {
    switch(baud_rate) {
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        default:
            throw IllegalConfigException("Unknown baud rate: ", baud_rate);
    }
}

BotConfigInfo parse_bot_device_config(Config& config, size_t width, size_t height) {
    if(!config.contains("bot.device.io_descriptor"))
        throw IllegalConfigException("No IO descriptor provided for hardware device backend");
    if(!config.contains("bot.device.baud_rate"))
        throw IllegalConfigException("No baud rate provided for hardware device backend");
    std::string filename = config.get("bot.device.io_descriptor")->getString();
    size_t baud_rate = config.get("bot.device.baud_rate")->getInteger();

    size_t canvas_width = config.putIfNew("bot.device.canvas_width", new ConfigInteger(width))->getInteger();
    size_t canvas_height = config.putIfNew("bot.device.canvas_width", new ConfigInteger(height))->getInteger();

    BotInfo bot_info;
    bot_info.device_name = filename;
    bot_info.baud_rate = get_baud_rate(baud_rate);
    return BotConfigInfo{new BotController(bot_info), nullptr, canvas_width, canvas_height};
}

BotConfigInfo parse_bot_bitprinter_config(Config& config, size_t width, size_t height) {
    std::string file_name = config.putIfNew("bot.bitprinter.output_file", new ConfigString("/dev/stdout"))->getString();
    std::ofstream* new_file = new std::ofstream(file_name);
    size_t canvas_width = config.putIfNew("bot.bitprinter.canvas_width", new ConfigInteger(width))->getInteger();
    size_t canvas_height = config.putIfNew("bot.bitprinter.canvas_width", new ConfigInteger(height))->getInteger();
    return BotConfigInfo{new BotBitPrinter(*new_file), new_file, canvas_width, canvas_height};
}

BotConfigInfo parse_bot_textprinter_config(Config& config, size_t width, size_t height) {
    std::string file_name = config.putIfNew("bot.textprinter.output_file", new ConfigString("/dev/stdout"))->getString();
    std::ofstream* new_file = new std::ofstream(file_name);
    size_t canvas_width = config.putIfNew("bot.textprinter.canvas_width", new ConfigInteger(width))->getInteger();
    size_t canvas_height = config.putIfNew("bot.textprinter.canvas_width", new ConfigInteger(height))->getInteger();
    return BotConfigInfo{new BotPrinter(*new_file), new_file, canvas_width, canvas_height};
}

BotConfigInfo parse_bot_config(Config& config) {
    const ConfigValue* value = config.putIfNew("bot.backend", new ConfigString("device"));

    size_t default_width = config.putIfNew("bot.canvas_width", new ConfigInteger(1500))->getInteger();
    size_t default_height = config.putIfNew("bot.canvas_height", new ConfigInteger(1500))->getInteger();

    std::string device_type = value->getString();
    if(device_type == "bitprinter")
        return parse_bot_bitprinter_config(config, default_width, default_height);
    else if(device_type == "textprinter")
        return parse_bot_textprinter_config(config, default_width, default_height);
    else
        return parse_bot_device_config(config, default_width, default_height);
}

BluetoothConfigInfo parse_bluetooth_config(Config& config) {
    size_t channel = config.putIfNew("bluetooth.channel", new ConfigInteger(1))->getInteger();
    return BluetoothConfigInfo{channel};
}

int main(int argc, char* argv[]) {
    if(argc < 2)
        return 0;

    ConfigParser parser(argv[1]);
    Config config = parser.parse();
    BotConfigInfo bot_config = parse_bot_config(config);
    BluetoothConfigInfo bluetooth_config = parse_bluetooth_config(config);
    BluetoothServer server(*bot_config.device, bluetooth_config.channel, bot_config.canvas_width, bot_config.canvas_height);
    server.start();

    delete bot_config.device;
    delete bot_config.file_stream;

    return 0;
}
