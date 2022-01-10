#include <iostream>
#include <netinet/in.h>
#include "util.h"

namespace util {

    void check(bool condition, const std::string& message_failure, const std::string& message_success){
        if (condition)
            std::cout << message_success;
        else {
            error(1, errno, "%s", message_failure.c_str());
            exit(EXIT_FAILURE);
        }
    }


    int initSocket(in_addr_t address, const std::string& port, bool isBind) {
        sockaddr_in sAddress{};
        sAddress.sin_family = AF_INET;
        sAddress.sin_addr.s_addr = address;
        sAddress.sin_port = htons(strtol(port.c_str(), nullptr, 0));
        int sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        check(sock > 0, "socket creation failed");

        if (isBind) {
            int bindRes = bind(sock, (struct sockaddr*)&sAddress, sizeof sAddress);
            check(bindRes >= 0, "socket bind failed");
        }

        return sock;
    }


    ssize_t readData(int fd, std::string& message, ssize_t size) {
        char* tmp{};
        auto ret = read(fd, tmp, size);
        if (ret == -1) error(1, errno, "read failed on descriptor %d", fd);
        message = tmp;
        return ret;
    }

    ssize_t readData(int fd, char * message, ssize_t size) {
        auto ret = read(fd, message, size);
        if (ret == -1) error(1, errno, "read failed on descriptor %d", fd);
        return ret;
    }

    void writeData(int fd, const std::string& message) {
        auto ret = write(fd, message.c_str(), message.size());
        if (ret == -1) error(1, errno, "write failed on descriptor %d", fd);
        if (ret != message.size()) error(0, errno, "wrote less than requested to descriptor %d (%ld/%ld)", fd, message.size(), ret);
    }

    void writeData(int fd, const char * message, ssize_t size) {
        auto ret = write(fd, message, size);
        if (ret == -1) error(1, errno, "write failed on descriptor %d", fd);
        if (ret != size) error(0, errno, "wrote less than requested to descriptor %d (%ld/%ld)", fd, size, ret);
    }
}