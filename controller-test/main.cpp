#include <fstream>

#include <winsock2.h>
#include <ws2bth.h>
#include <windows.h>
#include <bluetoothapis.h>

#include <iostream>
#include <memory>

BTH_ADDR string_to_bluetooth(const char* str) {
    UINT bluetooth_addr[6];
    sscanf(str, "%02x:%02x:%02x:%02x:%02x:%02x", &bluetooth_addr[0], &bluetooth_addr[1], &bluetooth_addr[2], &bluetooth_addr[3], &bluetooth_addr[4], &bluetooth_addr[5]);

    BTH_ADDR result = 0;
    for(size_t i = 0; i < 6; ++i) {
        BTH_ADDR temp = (BTH_ADDR)(bluetooth_addr[i] & 0xFF);
        result = result << 8 | temp;
    }
    return result;
}

uint8_t send_data(BTH_ADDR remote_addr, const uint8_t* data, size_t data_size) {
    SOCKADDR_BTH server_addr;
    ZeroMemory(&server_addr, sizeof(server_addr));

    server_addr.addressFamily = AF_BTH;
    server_addr.btAddr = remote_addr;
    server_addr.port = 1;

    SOCKET socket = ::socket(AF_BTH, SOCK_STREAM, BTHPROTO_RFCOMM);
    if(socket == INVALID_SOCKET) {
        std::cerr << "Socket call failed" << std::endl;
        return 2;
    }

    std::cout << "Connecting to address: " << std::hex << remote_addr << std::dec << std::endl;

    if(connect(socket, (struct sockaddr*)&server_addr, sizeof(SOCKADDR_BTH)) == SOCKET_ERROR) {
        DWORD error = WSAGetLastError();
        std::cerr << "Connect failed: " << error << std::endl;
        closesocket(socket);
        return 2;
    }
    //Send the size
    size_t result = send(socket, (const char*)&data_size, 8, 0);
    std::cout << "Sent " << result << " bytes in size field" << std::endl;
    result = send(socket, (const char*)data, data_size, 0);
    std::cout << "Sent " << result << " bytes" << std::endl;

    uint8_t result_byte;
    recv(socket, (char*)&result_byte, 1, 0);

    closesocket(socket);

    return result_byte;
}

int main(int argc, char* argv[]) {
    if(argc < 3)
        return 1;
    std::ifstream input(argv[1], std::ios::binary);

    BTH_ADDR bluetooth_addr = string_to_bluetooth(argv[2]); //e.g "XX:XX:XX:XX:XX:XX"

    input.seekg(0, std::ios::end);
    size_t file_size = input.tellg();
    input.seekg(0, std::ios::beg);

    std::unique_ptr<uint8_t[]> data(new uint8_t[file_size]);
    input.read((char*)data.get(), file_size);

    WSADATA winsock_data;
    if(WSAStartup(MAKEWORD(2,2), &winsock_data)) {
        std::cerr << "Failed to initialize winsock" << std::endl;
        return 0;
    }

    send_data(bluetooth_addr, data.get(), file_size);

    WSACleanup();
    return 0;
}
