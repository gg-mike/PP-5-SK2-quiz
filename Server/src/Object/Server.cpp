#include <sys/ioctl.h>
#include "pch.h"
#include "Server.h"


std::shared_ptr <Server> Server::GetInstance() {
    static const std::shared_ptr<Server> ServerInstance(new Server());
    return ServerInstance;
}

void Server::Create(char *_port, ssize_t _buffer_size) {
    if (this->fd != -1) {
        LOGWARNING("Server already created!");
        return;
    }
    buffer_size = _buffer_size;

    // Socket creation
    int on = 1;
    sockaddr_in sockaddrIn {
        AF_INET,
        htons(strtol(_port, nullptr, 0)),
        {.s_addr = INADDR_ANY}
    };
    fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (fd < 0) {
        LOGERROR("Server: socket() failed");
        exit(EXIT_FAILURE);
    }

    if (setsockopt(fd, SOL_SOCKET,  SO_REUSEADDR, (char *)&on, sizeof(on)) < 0) {
        LOGERROR("Server: setsockopt() failed");
        close(fd);
        exit(EXIT_FAILURE);
    }

    if (ioctl(fd, FIONBIO, (char *)&on) < 0) {
        LOGERROR("Server: ioctl() failed");
        close(fd);
        exit(EXIT_FAILURE);
    }

    if (bind(fd, (struct sockaddr*)&sockaddrIn, sizeof sockaddrIn) < 0) {
        LOGERROR("Server: bind() failed");
        close(fd);
        exit(EXIT_FAILURE);
    }

    LOGINFO("Server: started at port: ", _port, " (fd=", this->fd, ")");
}

void Server::Listen(int conn_num) const {
    if (listen(fd, conn_num) < 0) {
        LOGERROR("Server: listen() failed");
        close(fd);
        exit(EXIT_FAILURE);
    }
    LOGINFO("Server: listening for connections");
}

int Server::Accept() const {
    int acc_fd = accept(fd, nullptr, nullptr);
    if (acc_fd >= 0)
        LOGINFO("Server: accepted connection (fd=", acc_fd, ")");
    return acc_fd;
}

Server::~Server() {
    LOGINFO("Server: shutdown");
    close(fd);
}

void signal_callback_handler(int sig) {
    LOGINFO("Recv signal ", sig, "; Exiting...");
    Server::GetInstance()->running = false;
}

void Server::Poll() {
    ssize_t rc;
    int acc_fd;
    bool closeConn;

    running = true;
    signal(SIGINT, signal_callback_handler);

    std::vector<pollfd> polls {{{fd, POLLIN}}};
    std::vector<pollfd> nextPolls{};


    std::string buffer;

    while (running) {
        if (poll(polls.data(), polls.size(), -1) < 0) {
            LOGERROR("Server: poll() failed");
            break;
        }

        nextPolls.push_back(polls.at(0));

        for (auto& pfd : polls) {
            if (pfd.revents == 0)
                continue;

            if (pfd.revents != POLLIN) {
                LOGERROR("Server: revents = ", pfd.revents);
                running = false;
                break;
            }

            if (pfd.fd == fd) {
                do {
                    acc_fd = Accept();
                    if (acc_fd < 0) {
                        if (errno != EWOULDBLOCK) {
                            LOGERROR("Server: accept() failed");
                            running = false;
                        }
                        break;
                    }
                    nextPolls.push_back({acc_fd, POLLIN});
                } while (acc_fd != -1);
            }
            else {
                closeConn = false;

                do {
                    rc = Read(pfd.fd, buffer);
                    if (rc < 0) {
                        closeConn = errno != EWOULDBLOCK;
                        break;
                    } else if (rc == 0) {
                        LOGINFO("Server: connection closed");
                        closeConn = true;
                        break;
                    }
                    Write(1, buffer);

                } while (true);

                if (closeConn) {
                    shutdown(pfd.fd, SHUT_RDWR);
                    close(pfd.fd);
                } else
                    nextPolls.push_back(pfd);
            }
        }
        polls = nextPolls;
        nextPolls.clear();
    }
}

ssize_t Server::Read(int _fd, std::string &message) const {
    char tmp[buffer_size];
    auto ret = read(_fd, tmp, buffer_size);
    LOGINFO("Read: ", ret);
    if (ret == -1)
        LOGERROR("Server: read() failed on fd=", _fd);
    message = std::string(tmp);
    return ret;
}

ssize_t Server::Recv(int _fd, std::string &message) const {
    char tmp[buffer_size];
    auto ret = recv(_fd, tmp, buffer_size, 0);
    if (ret == -1 && errno != EWOULDBLOCK)
        LOGERROR("Server: recv() failed on fd=", _fd);
    message = std::string(tmp);
    return ret;
}

void Server::Write(int _fd, const std::string &message) const {
    auto ret = write(_fd, message.data(), message.size());
    if (ret == -1)
        LOGERROR("Server: write() failed on fd=", _fd);
    if (ret != message.size())
        LOGERROR("Server: wrote less than requested to descriptor ", _fd, "(", ret, "/",  buffer_size, ")");
}
