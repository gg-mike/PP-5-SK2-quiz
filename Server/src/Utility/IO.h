#pragma once

namespace IO {

    ssize_t Recv(int fd, std::string& message, ssize_t size);

    void Write(int fd, const std::string& message, ssize_t size);

}
