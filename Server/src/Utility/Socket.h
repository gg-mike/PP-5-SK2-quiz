#pragma once

constexpr static int on = 1;

namespace Socket {

    int Create();

    void SetAsReuseAddr(int fd);

    void SetAsNonblocking(int fd);

    void Bind(int fd, int port);

    void Listen(int fd, int connectionNumber);

    void Shutdown(int fd);

}
