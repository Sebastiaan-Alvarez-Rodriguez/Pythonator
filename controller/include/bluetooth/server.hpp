#ifndef _BLUETOOTH_SERVER_HPP_
#define _BLUETOOTH_SERVER_HPP_

#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>

#include "utils/queuelock.hpp"

class BotDevice;

class BluetoothServer {
    private:
        int socket;
        struct sockaddr_rc address;
        volatile bool active = true;
        BotDevice& bot_controller;
        QueueLock queue_lock;
        size_t canvas_width, canvas_height;

        void handle(int, struct sockaddr_rc);
    public:
        BluetoothServer(BotDevice&, size_t, size_t, size_t);
        ~BluetoothServer();

        void start();
};

#endif // _BLUETOOTH_SERVER_HPP_
