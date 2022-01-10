#include "pch.h"
#include "Server.h"

#include "util.h"

std::shared_ptr <Server> Server::GetInstance() {
    static const std::shared_ptr<Server> ServerInstance(new Server());
    return ServerInstance;
}

void Server::Create(char *port) {
    if (this->fd != -1)
    {
        std::cout << "Server already created!" << std::endl;
        return;
    }

    this->fd = util::initSocket(INADDR_ANY, port, true);
    std::cout << "Server: started at port: " << port << " (fd=" << this->fd << ")"<< std::endl;
}

void Server::Listen(int conn_num) {
    listen(this->fd, conn_num);
    std::cout << "Server: listening for connections" << std::endl;
}

int Server::Accept() {
    int acc_fd = accept(this->fd, nullptr, nullptr);
    std::cout << "Server: accepted connection (fd=" << acc_fd << ")" << std::endl;
    return acc_fd;
}

Server::~Server() {
    std::cout << "Server: shutdown" << std::endl;
    close(this->fd);
}
