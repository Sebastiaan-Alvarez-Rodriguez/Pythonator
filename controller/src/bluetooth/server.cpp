#include "bluetooth/server.hpp"
#include "error/exceptions.hpp"
#include "image/processor.hpp"
#include "bot/controller.hpp"
#include "error/logging.hpp"

#include <unistd.h>
#include <sys/socket.h>

#include <cstring>
#include <thread>

//Fix BDADDR_ANY for C++
#ifdef __cplusplus
bdaddr_t _bd_addr_any = {{0, 0, 0, 0, 0, 0}};

#undef BDADDR_ANY
#define BDADDR_ANY (&_bd_addr_any)
#endif

BluetoothServer::BluetoothServer(BotDevice& bot_controller) : bot_controller(bot_controller) {
    std::memset(&this->address, 0, sizeof(this->address));
    this->socket = ::socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
    if(this->socket == -1)
        throw BluetoothException("Failed to create the bluetooth socket");

    //Bind to channel 1
    this->address.rc_family = AF_BLUETOOTH;
    this->address.rc_channel = 1;
    bacpy(&this->address.rc_bdaddr, BDADDR_ANY);

    if(::bind(this->socket, (struct sockaddr*)&this->address, sizeof(this->address)))
        throw BluetoothException("Failed to bind to RFCOMM port");
}

BluetoothServer::~BluetoothServer() {
    close(this->socket);
}

void BluetoothServer::start() {
    //Start listening
    if(::listen(this->socket, 0) == -1)
        throw BluetoothException("Failed to listen on the bluetooth socket");

    while(this->active) {
        struct sockaddr_rc client_addr;
        std::memset(&client_addr, 0, sizeof(client_addr));
        socklen_t socket_size = sizeof(client_addr);

        int client = ::accept(this->socket, (struct sockaddr*)&client_addr, &socket_size);
        if(client == -1)
            continue;

        std::thread handler_thread(&BluetoothServer::handle, this, client, client_addr);
        handler_thread.detach();
    }
}

void BluetoothServer::handle(int socket, struct sockaddr_rc remote_addr) {
    try {
        do {
            uint64_t image_size;
            if(::recv(socket, &image_size, sizeof(image_size), 0) != sizeof(uint64_t))
                throw BluetoothException("Failed to read size of image");

            if(image_size == 0)
                break;

            std::unique_ptr<uint8_t[]> data(new uint8_t[image_size]);
            ssize_t result = ::recv(socket, data.get(), image_size, 0);
            if(result == -1 || (size_t)result != image_size)
                throw BluetoothException("Failed to receive image");

            ImageProcessor processor(data.get(), image_size);
            processor.transform();

            uint8_t status = (uint8_t)this->bot_controller.writeLines(processor.getData());
            if(::send(socket, &status, sizeof(status), 0) != sizeof(uint8_t))
                throw BluetoothException("Failed to send result status code");
        } while(true);
    }
    catch(const std::runtime_error& except) {
        log_warning("Failed to process socket request: %s\n", except.what());
    }
    close(socket);
}
