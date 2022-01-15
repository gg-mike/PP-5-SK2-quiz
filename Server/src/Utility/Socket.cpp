#include "pch.h"
#include "Socket.h"

namespace Socket {

    int Create() {
        int fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        if (fd < 0) {
            LOGERROR("Socket::Create() failed");
            exit(EXIT_FAILURE);
        }
        return fd;
    }

    void SetAsReuseAddr(int fd) {
        if (setsockopt(fd, SOL_SOCKET,  SO_REUSEADDR, (char *)&on, sizeof(on)) < 0) {
            LOGERROR("Socket::SetAsReuseAddr() failed");
            close(fd);
            exit(EXIT_FAILURE);
        }
    }

    void SetAsNonblocking(int fd) {
        if (fcntl(fd, F_SETFL, fcntl(fd, F_GETFL) | O_NONBLOCK) < 0) {
            LOGERROR("Socket::SetAsNonblocking() failed");
            close(fd);
            exit(EXIT_FAILURE);
        }
    }

    void Bind(int fd, int port) {
        sockaddr_in sockaddrIn {
                AF_INET,
                htons(port),
                {.s_addr = INADDR_ANY}
        };

        if (bind(fd, (struct sockaddr*)&sockaddrIn, sizeof sockaddrIn) < 0) {
            LOGERROR("Socket::Bind() failed");
            close(fd);
            exit(EXIT_FAILURE);
        }
    }

    void Listen(int fd, int connectionNumber) {
        if (listen(fd, connectionNumber) < 0) {
            LOGERROR("Socket::Listen() failed");
            close(fd);
            exit(EXIT_FAILURE);
        }
    }

}
