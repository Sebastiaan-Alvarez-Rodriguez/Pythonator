#ifndef _BLUETOOTH_SERVER_HPP_
#define _BLUETOOTH_SERVER_HPP_

#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>

class BotDevice;

class BluetoothServer {
    private:
        int socket;
        struct sockaddr_rc address;
        volatile bool active = true;
        BotDevice& bot_controller;

        void handle(int, struct sockaddr_rc);
    public:
        BluetoothServer(BotDevice&);
        ~BluetoothServer();

        void start();
};

#endif // _BLUETOOTH_SERVER_HPP_
