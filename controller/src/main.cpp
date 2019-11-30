#include "bluetooth/server.hpp"
#include "bot/controller.hpp"
#include "bot/printer.hpp"
#include "config/parser.hpp"

#include <iostream>

int main(int argc, char* argv[]) {
    if(argc < 2)
        return 0;
    
    //TODO: reenable config parser after debugging
    //ConfigParser parser(argv[1]);
    BotPrinter printer(std::cout);
    BluetoothServer server(printer);
    server.start();

    return 0;
}
