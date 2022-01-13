#include "pch.h"
#include "Server.h"

#include "nlohmann/json.hpp"


std::shared_ptr <Server> Server::GetInstance() {
    static const std::shared_ptr<Server> ServerInstance(new Server());
    return ServerInstance;
}


void Server::Run(const std::string &configFile) {
    Init(configFile);
    Listen();
    Poll();
}


void Server::Init(const std::string &configFile) {
    if (state != State::None) {
        LOGWARNING("Server already initialized!");
        return;
    }
    ReadConfig(configFile);

    int on = 1;
    sockaddr_in sockaddrIn {
        AF_INET,
        htons(config.port),
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

    LOGINFO("Server: started at port: ", config.port, " (fd=", fd, ")");
    state |= State::Inited;
}

void Server::Listen(int conn_num) {
    if (conn_num == USE_CONFIG)
        conn_num = config.listenConnectionNumber;
    if (listen(fd, conn_num) < 0) {
        LOGERROR("Server: listen() failed");
        close(fd);
        exit(EXIT_FAILURE);
    }
    LOGINFO("Server: listening for connections");
    state |= State::Listening;
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
    Server::GetInstance()->state &= ~Server::State::Running;
}

void Server::Poll() {
    ssize_t rc;
    int acc_fd;

    state |= State::Running;
    signal(SIGINT, signal_callback_handler);

    std::vector<pollfd> polls {{{fd, POLLIN}}};
    std::vector<pollfd> nextPolls{};


    std::string buffer;

    while (state & State::Running) {
        if (poll(polls.data(), polls.size(), -1) < 0) {
            LOGERROR("Server: poll() failed");
            break;
        }

        nextPolls.push_back({fd, POLLIN});

        for (auto& pfd : polls) {
            if (pfd.revents == 0)
                continue;

            if (pfd.revents != POLLIN) {
                LOGERROR("Server: revents = ", pfd.revents);
                state &= ~State::Running;
                break;
            }

            if (pfd.fd == fd) {
                do {
                    acc_fd = Accept();
                    if (acc_fd < 0) {
                        if (errno != EWOULDBLOCK) {
                            LOGERROR("Server: accept() failed");
                            state &= ~State::Running;
                        }
                        break;
                    }
                    nextPolls.push_back({acc_fd, POLLIN});
                } while (acc_fd != -1);
            }
            else {
                rc = Recv(pfd.fd, buffer);
                if ((rc < 0 && errno != EWOULDBLOCK) || rc == 0) {
                    LOGINFO("Server: connection closed on fd=", pfd.fd);
                    shutdown(pfd.fd, SHUT_RDWR);
                    close(pfd.fd);
                } else {
                    LOGINFO("From fd=", pfd.fd, ": ", buffer);
                    nextPolls.push_back(pfd);
                }
            }
        }
        polls = nextPolls;
        nextPolls.clear();
    }
}

ssize_t Server::Read(int _fd, std::string &message) const {
    char tmp[config.bufSize];
    auto ret = read(_fd, tmp, config.bufSize);
    if (ret == -1)
        LOGERROR("Server: read() failed on fd=", _fd);
    message = std::string(tmp, 0, ret - 1);
    return ret;
}

ssize_t Server::Recv(int _fd, std::string &message) const {
    char tmp[config.bufSize];
    auto ret = recv(_fd, tmp, config.bufSize, 0);
    if (ret == -1 && errno != EWOULDBLOCK)
        LOGERROR("Server: recv() failed on fd=", _fd);
    message = std::string(tmp, 0, ret - 1);
    return ret;
}

void Server::Write(int _fd, const std::string &message) const {
    auto ret = write(_fd, message.c_str(), message.size());
    if (ret == -1)
        LOGERROR("Server: write() failed on fd=", _fd);
    if (ret != message.size())
        LOGERROR("Server: wrote less than requested to descriptor ", _fd, "(", ret, "/", message.size(), ")");
}

void Server::ReadConfig(const std::string &configFile) {
    using nlohmann::json;
    std::ifstream ifs(configFile);
    json j;
    ifs >> j;
    config = Config{
        j["port"],
        j["bufSize"],
        j["listenConnectionNumber"]
    };
    ifs.close();
}
