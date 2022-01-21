#include "pch.h"
#include "IO.h"

namespace IO {

    ssize_t Read(int fd, std::string& message, ssize_t size) {
        char tmp[size];
        auto ret = read(fd, tmp, size);
        if (ret == -1)
            LOGERROR("IO::Read() failed on fd=", fd);
        message = std::string(tmp, 0, ret - 1);
        return ret;
    }

    ssize_t Read(int fd, char* message, ssize_t size) {
        auto ret = read(fd, message, size);
        if (ret == -1)
            LOGERROR("IO::Read() failed on fd=", fd);
        return ret;
    }


    ssize_t Recv(int fd, std::string& message, ssize_t size) {
        char tmp[size];
        auto ret = recv(fd, tmp, size, 0);
        if (ret == -1 && errno != EWOULDBLOCK)
            LOGERROR("IO::Recv() failed on fd=", fd);
        message = std::string(tmp, 0, ret - 1);
        return ret;
    }

    ssize_t Recv(int fd, char* message, ssize_t size) {
        auto ret = recv(fd, message, size, 0);
        if (ret == -1 && errno != EWOULDBLOCK)
            LOGERROR("IO::Recv() failed on fd=", fd);
        return ret;
    }

    void Write(int fd, const std::string& message, ssize_t size) {
        std::string m = "<$begin$>" + message + "<$end$>\n";
        auto ret = write(fd, m.c_str(), std::strlen(m.c_str()));
        if (ret == -1)
            LOGERROR("IO::Write() failed on fd=", fd);
        if (ret != size)
            LOGERROR("IO::Write() wrote less than requested to descriptor ", fd, "(", ret, "/", size, ")");
    }

    void Write(int fd, char* message, ssize_t size) {
        auto ret = write(fd, message, size);
        if (ret == -1)
            LOGERROR("IO::Write() failed on fd=", fd);
        if (ret != size)
            LOGERROR("IO::Write() wrote less than requested to descriptor ", fd, "(", ret, "/", size, ")");
    }

}
