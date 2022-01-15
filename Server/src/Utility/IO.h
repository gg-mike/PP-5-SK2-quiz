#pragma once

namespace IO {

    ssize_t Read(int fd, std::string& message, ssize_t size);
    ssize_t Read(int fd, char* message, ssize_t size);

    ssize_t Recv(int fd, std::string& message, ssize_t size);
    ssize_t Recv(int fd, char* message, ssize_t size);

    void Write(int fd, const std::string& message, ssize_t size);
    void Write(int fd, char* message, ssize_t size);

}
