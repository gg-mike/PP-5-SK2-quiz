#include "pch.h"
#include "Client.h"

#include "Server.h"
#include "nlohmann/json.hpp"
#include "Game/Participants/Host.h"
#include "Game/Participants/Player.h"
#include "Enumerators/MessageType.h"

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
        Server::GetInstance()->ClientRequest(fd, Enumerators::ClientRequest::SHUTDOWN);
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
            Server::GetInstance()->ClientRequest(fd, Enumerators::ClientRequest::SHUTDOWN);
            running = false;
            return;
        }
        else if (rc > 0) {
            lastHeartbeat = high_resolution_clock::now();
            ProcessRequest(nlohmann::json::parse(buffer));
        }
    }
}

void Client::ProcessRequest(const nlohmann::json &request) {
    if (gameParticipant == nullptr) {
        Enumerators::MessageType messageType = request["type"];
        nlohmann::json response;
        switch (messageType) {
            case Enumerators::BECOME_HOST:
                gameParticipant = std::make_shared<Host>();
                response = {
                        {"type", Enumerators::ACCEPT},
                        {"desc", "Client accepted as a host"}
                };
                break;
            case Enumerators::BECOME_PLAYER:
                gameParticipant = std::make_shared<Player>();
                response = {
                        {"type", Enumerators::ACCEPT},
                        {"desc", "Client accepted as a player"}
                };
                break;
            default:
                response = {
                        {"type", Enumerators::ERROR},
                        {
                            "desc", "Expected BECOME_HOST (" + std::to_string(Enumerators::BECOME_HOST) +
                                    ") or BECOME_PLAYER (" + std::to_string(Enumerators::BECOME_PLAYER) + ")"
                        }
                };
        }
        Server::GetInstance()->Send(fd, response.dump(4));
        return;
    }

    nlohmann::json response{gameParticipant->Process(request)};
    Server::GetInstance()->Send(fd, response.dump(4));
}

void Client::SendShutdownMessage() const {
    nlohmann::json response{{"text", "Shutdown"}};
    Server::GetInstance()->Send(fd, response.dump(4));
}
