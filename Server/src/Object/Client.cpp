#include "pch.h"
#include "Client.h"

#include "nlohmann/json.hpp"

#include "Server.h"
#include "Database.h"
#include "Game/Participants/Host.h"
#include "Game/Participants/Player.h"
#include "Enumerators/Request.h"

using std::chrono::high_resolution_clock, std::chrono::duration_cast;
using Enumerators::ServerAction, Enumerators::Request;
using nlohmann::json;

Client::Client(int fd)
    : fd(fd), lastHeartbeat(high_resolution_clock::now()) {
    maxTimeBetweenHb_ms = Server::GetInstance()->GetConfig().maxTimeBetweenHb_sec * 1000;
    messageBegin = Server::GetInstance()->GetConfig().messageBegin;
    messageEnd = Server::GetInstance()->GetConfig().messageEnd;
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
        Server::GetInstance()->Action(fd, ServerAction::CLOSE_CONN);
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
            Server::GetInstance()->Action(fd, ServerAction::CLOSE_CONN);
            running = false;
            return;
        }
        else if (rc > 0) {
            lastHeartbeat = high_resolution_clock::now();

            auto requestStart = buffer.find(messageBegin);
            auto requestEnd = buffer.find(messageEnd);
            if (requestStart == std::string::npos) {
                Server::GetInstance()->Send(fd, {
                        {"type", Request::ERROR},
                        {"desc", "Request wasn't properly started"}
                });
                continue;
            }
            else if (requestEnd == std::string::npos) {
                Server::GetInstance()->Send(fd, {
                        {"type", Request::ERROR},
                        {"desc", "Request wasn't properly ended"}
                });
                continue;
            }

            try {
                std::string message = buffer.substr(requestStart + messageBegin.size(), requestEnd - requestStart - messageBegin.size());
                ProcessRequest(json::parse(message));
            } catch (json::exception& exception) {
                Server::GetInstance()->Send(fd, {
                        {"type", Request::ERROR},
                        {"desc", "json.exception.parse_error"},
                        {"exceptionMessage", exception.what()}
                });
            }
        }
    }
}

void Client::ProcessRequest(const json &request) {
    Json::Field<int> type = Json::Test<int>(request, "type", fd, Request::NONE);
    if (type.opStatus != 1) return;

    if (type.value == Request::HEARTBEAT) return;
    // In the future we can log what client accepted, for now it is not required
    if (type.value & Request::ACCEPT) return;

    if (gameParticipant == nullptr) {
        json response{};
        switch (type.value) {
            case Request::CREATE_GAME: {
                gameParticipant = std::make_shared<Host>(fd);
                int gameCode = Database::GetInstance()->CreateNewGame(std::dynamic_pointer_cast<Host>(gameParticipant));
                std::dynamic_pointer_cast<Host>(gameParticipant)->SetGameCode(gameCode);
                response = {
                        {"type",     Request::CREATE_GAME | Request::ACCEPT},
                        {"gameCode", gameCode}
                };
                break;
            }
            case Request::JOIN_GAME: {

                Json::Field<int> gameCode = Json::Test<int>(request, "gameCode", fd,Request::JOIN_GAME);
                if (gameCode.opStatus != 1) return;

                Json::Field<std::string> nick = Json::Test<std::string>(request, "nick", fd, Request::JOIN_GAME);
                if (nick.opStatus != 1) return;

                if (!Database::GetInstance()->GameExists(gameCode.value))
                    response = {
                            {"type", Request::JOIN_GAME | Request::DECLINE},
                            {"desc", "Game with code #" + std::to_string(gameCode.value) + " does not exists"}
                    };
                else if (!Database::GetInstance()->NickFree(gameCode.value, nick.value))
                    response = {
                            {"type", Request::JOIN_GAME | Request::DECLINE},
                            {"desc", "Nick \"" + nick.value + "\" is taken"}
                    };
                else {
                    gameParticipant = std::make_shared<Player>(fd, gameCode.value, nick.value);
                    Database::GetInstance()->JoinGame(gameCode.value, std::dynamic_pointer_cast<Player>(gameParticipant));
                    response = {
                            {"type", Request::JOIN_GAME | Request::ACCEPT},
                            {"desc", "Joined game #" + std::to_string(gameCode.value) + " with nick \"" +
                                     nick.value +
                                     "\""}
                    };
                }
                break;
            }
            case Request::EXIT: {
                Server::GetInstance()->Send(fd, {{"type", Request::EXIT | Request::ACCEPT}});
                Server::GetInstance()->Action(fd, ServerAction::CLOSE_CONN);
                return;
            }
            default:
                response = {
                        {"type", Request::ERROR},
                        {"desc", "Expected CREATE_GAME, JOIN_GAME or EXIT"}
                };
        }
        Server::GetInstance()->Send(fd, response);
        return;
    }
    json response = gameParticipant->Process(static_cast<Request>(type.value), request);
    if (!response.empty())
        Server::GetInstance()->Send(fd, response);
}

void Client::SendShutdownMessage() const {
    Server::GetInstance()->Send(fd, {{"text", "Connection closed"}});
}
