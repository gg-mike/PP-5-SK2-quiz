#include "pch.h"
#include "Client.h"

#include "Server.h"
#include "nlohmann/json.hpp"

using std::chrono::high_resolution_clock, std::chrono::duration_cast;

Client::Client(int fd)
    : fd(fd), lastHeartbeat(high_resolution_clock::now()) {
    maxTimeBetweenHb_ms = Server::GetInstance()->GetConfig().maxTimeBetweenHb_sec * 1000;
    heartbeatThread = std::thread(&Client::CheckHeartbeat, this);
    readerThread = std::thread(&Client::ReadMessages, this);
}

void Client::Shutdown() {
    running = false;
    SendShutdownMessage();
    Socket::Shutdown(fd);
    heartbeatThread.join();
    readerThread.join();
    LOGINFO("Client #", fd, ": connection closed");
}

void Client::CheckHeartbeat() {
    while (running) {
        std::this_thread::sleep_for(std::chrono::seconds(1));

        if (duration_cast<std::chrono::milliseconds>(high_resolution_clock::now() - lastHeartbeat.load())
                .count() < maxTimeBetweenHb_ms) continue;

        if (!running) return;

        LOGWARNING("Client #", fd, " is not responding => closing connection");
        Server::GetInstance()->ClientRequest(fd, Server::Request::SHUTDOWN);
        running = false;
        return;
    }
}

void Client::ReadMessages() {
    ssize_t rc;
    std::string buffer;
    while (running) {
        rc = IO::Recv(fd, buffer, Server::GetInstance()->GetConfig().bufSize);

        if ((rc < 0 && errno != EWOULDBLOCK) || rc == 0) {
            if (!running) return;

            LOGWARNING("Client #", fd, " has disconnected => closing connection");
            Server::GetInstance()->ClientRequest(fd, Server::Request::SHUTDOWN);
            running = false;
            return;
        }
        else if (rc > 0) {
            lastHeartbeat = high_resolution_clock::now();
            ProcessMessage(buffer);
        }
    }
}

void Client::ProcessMessage(const std::string &message) const {
    nlohmann::json response{
            {"text", message},
            {"size", message.size()}
    };

    Server::GetInstance()->Send(fd, response.dump(4));
}

void Client::SendShutdownMessage() const {
    nlohmann::json response{
            {"text", "Shutdown"}
    };

    Server::GetInstance()->Send(fd, response.dump(4));
}
