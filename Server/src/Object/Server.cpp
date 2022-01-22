#include "pch.h"
#include "Server.h"

#include "nlohmann/json.hpp"

void SignalCallbackHandler(int) {
    std::cout << "\r";
    LOGINFO("Server: shutdown started");
    Server::GetInstance()->running = false;
    Server::GetInstance()->cv.notify_one();
}

std::shared_ptr <Server> Server::GetInstance() {
    static const std::shared_ptr<Server> ServerInstance(new Server());
    return ServerInstance;
}

void Server::Run(const std::string &configFile) {
    Init(configFile);

    acceptThread = std::thread(&Server::AcceptClients, this);
    handleThread = std::thread(&Server::HandleClients, this);
}

void Server::Shutdown() {
    acceptThread.join();
    handleThread.join();

    for (auto &client: clients)
        client.second->Shutdown();

    close(fd);
    LOGINFO("Server: shutdown ended");
}

void Server::Action(int clientFd, Enumerators::ServerActionCode actionCode) {
    if (actionCode & Enumerators::ServerAction::CLOSE_CONN) {
        std::unique_lock _{handleMtx};
        clientsToShutdown.insert(clientFd);
        cv.notify_one();
    }
}

void Server::Send(int receiverFd, const nlohmann::json& message, int offset) const {
    std::string fullMessage = config.messageBegin + message.dump(offset) + config.messageEnd + "\n";

    ssize_t size = std::min(config.bufSize, (long)fullMessage.size());
    IO::Write(receiverFd, fullMessage + "\n", size);
}

void Server::Init(const std::string &configFile) {
    if (fd != -1) {
        LOGWARNING("Server already initialized!");
        return;
    }
    ReadConfig(configFile);

    fd = Socket::Create();
    Socket::SetAsReuseAddr(fd);
    Socket::SetAsNonblocking(fd);
    Socket::Bind(fd, config.port);
    Socket::Listen(fd, config.listenConnNum);

    running = true;
    signal(SIGINT, SignalCallbackHandler);

    LOGINFO("Server: started at port: ", config.port, " (fd=", fd, ")");
}

void Server::AcceptClients() {
    int acc_fd;
    while (running) {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        if ((acc_fd = accept(fd, nullptr, nullptr)) >= 0) {
            LOGINFO("Server: accepted connection (fd=", acc_fd, ")");
            std::lock_guard _{clientsMtx};
            clients.insert({acc_fd, std::make_shared<Client>(acc_fd)});
        }
        else if (errno != EWOULDBLOCK) {
            LOGERROR("Server: accept() failed");
            running = false;
        }
    }
}

void Server::HandleClients() {
    while (running) {
        std::unique_lock lock{handleMtx};
        cv.wait(lock, [&](){ return !clientsToShutdown.empty() || !running; });

        if (!running) return;

        std::lock_guard _{clientsMtx};
        for (auto clientToShutdown: clientsToShutdown) {
            if (!clients.contains(clientToShutdown))
                continue;

            clients.at(clientToShutdown)->Shutdown();
            clients.erase(clientToShutdown);
            LOGINFO("Server: closed connection with client #", clientToShutdown);
        }
        clientsToShutdown.clear();
    }
}

void Server::ReadConfig(const std::string &configFile) {
    std::ifstream ifs(configFile);
    nlohmann::json j;
    ifs >> j;
    config = Config{
        j["port"],
        j["bufSize"],
        j["listenConnNum"],
        j["maxTimeBetweenHb_sec"],
        j["messageBegin"],
        j["messageEnd"]
    };
    ifs.close();
}
