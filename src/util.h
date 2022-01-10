#pragma once

#include <unistd.h>
#include <cerrno>
#include <error.h>
#include <string>

namespace util {

    const ssize_t BUFFER_SIZE{64};

    void check(bool condition, const std::string& message_failure, const std::string& message_success = "");


    int initSocket(in_addr_t address, const std::string& port, bool isBind);


    ssize_t readData(int fd, std::string& message, ssize_t size = BUFFER_SIZE);
    ssize_t readData(int fd, char * message, ssize_t size);

    void writeData(int fd, const std::string& message);
    void writeData(int fd, const char * message, ssize_t size);
}
