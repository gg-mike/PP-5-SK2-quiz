#include "pch.h"
#include "IO.h"

namespace IO {

    ssize_t Recv(int fd, std::string& message, ssize_t size) {
        std::vector<char> rcvBuf;    // Allocate a receive buffer
        rcvBuf.resize(size,0x00);
        auto ret = recv(fd, &(rcvBuf[0]), size, 0);
        if (ret == -1 && errno != EWOULDBLOCK)
            LOGERROR("IO::Recv() failed on fd=", fd);
        message = std::string(&(rcvBuf[0]), 0, ret - 1);
        return ret;
    }

    void Write(int fd, const std::string& message, ssize_t size) {
        auto ret = write(fd, message.c_str(), size);
        if (ret == -1)
            LOGERROR("IO::Write() failed on fd=", fd);
        if (ret != size)
            LOGERROR("IO::Write() wrote less than requested to descriptor ", fd, "(", ret, "/", size, ")");
    }

}
