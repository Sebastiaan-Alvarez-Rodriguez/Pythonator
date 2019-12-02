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

//Utility function
ssize_t receiveFully(int socket, void* buffer, size_t buffer_size) {
    size_t result = 0;

    while(result < buffer_size) {
        ssize_t temp = ::recv(socket, (char*)buffer + result, buffer_size - result, 0);
        if(temp < 0)
            return temp;
        result += temp;
    }
    return result;
}

BluetoothServer::BluetoothServer(BotDevice& bot_controller, size_t channel, size_t canvas_width, size_t canvas_height)
                    : bot_controller(bot_controller), canvas_width(canvas_width), canvas_height(canvas_height) {
    std::memset(&this->address, 0, sizeof(this->address));
    this->socket = ::socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
    if(this->socket == -1)
        throw BluetoothException("Failed to create the bluetooth socket");

    //Bind to channel 1
    this->address.rc_family = AF_BLUETOOTH;
    this->address.rc_channel = channel;
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
            log_info("Received image request for image with size: %llu from %06X", (long long unsigned)image_size, remote_addr.rc_bdaddr);


            std::unique_ptr<uint8_t[]> data(new uint8_t[image_size]);
            ssize_t result = receiveFully(socket, data.get(), image_size);
            if(result == -1 || (size_t)result != image_size)
                throw BluetoothException("Failed to receive image, returned status: ", result);

            log_info("Received image from %06X", remote_addr.rc_bdaddr);

            ImageProcessor processor(data.get(), image_size);
            processor.transform(this->canvas_width, this->canvas_height);
            log_info("Finished transformation of image for %06X", remote_addr.rc_bdaddr);

            processor.optimize();
            log_info("Finished routing optimization of image for %06X", remote_addr.rc_bdaddr);

            //Acquire exclusive access to the bot
            this->queue_lock.lock();
            try {
                log_info("Writing result of client %06X to bot", remote_addr.rc_bdaddr);
                uint8_t status = (uint8_t)this->bot_controller.writeLines(processor.getData());
                if(::send(socket, &status, sizeof(status), 0) != sizeof(uint8_t))
                    throw BluetoothException("Failed to send result status code");
            }
            catch(...) {
                this->queue_lock.unlock();
                throw;
            }

            log_info("Finished handling request for %06X", remote_addr.rc_bdaddr);
        } while(true);
    }
    catch(const cv::Exception& except) {
        log_warning("OpenCV exception during handling of request: %s", except.what());
    }
    catch(const std::bad_alloc& alloc) {
        log_warning("Allocation failed when handling request: %s", alloc.what());
    }
    catch(const std::runtime_error& except) {
        log_warning("Failed to process socket request: %s", except.what());
    }
    close(socket);
}
