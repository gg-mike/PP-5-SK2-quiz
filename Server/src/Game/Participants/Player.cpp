#include "pch.h"
#include "Player.h"

#include "Enumerators/Request.h"
#include "Server/Database.h"

using nlohmann::json;
using Enumerators::GameState, Enumerators::Request;

Player::Player(int fd, std::atomic<bool>& stateChange, int gameCode, std::string nick)
    : fd(fd), stateChange(stateChange), gameCode(gameCode), nick(std::move(nick)) {}

json Player::Process(Request type, const json& request) {
    auto db = Database::GetInstance();
    switch (type) {
        case Request::ACCEPT:
        case Request::DECLINE:
        case Request::ERROR:
        case Request::NONE:         return {};
        case Request::EXIT_GAME:    return db->PlayerExit(gameCode, nick);
        case Request::ANSWER:       return db->PlayerAnswered(gameCode, nick, request);
        default: return {
            {"type", Request::ERROR},
            {"desc", "Unexpected request type"}
        };
    }
}

void Player::RequestHandler(GameState currGameState) {
    auto sv = Server::GetInstance();
    switch (currGameState) {
        case GameState::STARTED:
            return sv->Send(fd, {{"type", Request::GAME_STARTED}});
        case GameState::R_STARTED: {
            answered = false;
            wasCorrectAnswer = false;
            return sv->Send(fd, {{"type", Request::ROUND_STARTED}});
        }
        case GameState::R_ENDED: {
            if (answered)
                return sv->Send(fd, {
                        {"type", Request::ROUND_ENDED},
                        {"wasCorrectAnswer", wasCorrectAnswer.load()},
                        {"score", score.load()},
                        {"placeInRanking", pos.load()}
                });
            else
                return sv->Send(fd, {
                        {"type", Request::ROUND_TIMEOUT},
                        {"wasCorrectAnswer", false},
                        {"score", score.load()},
                        {"placeInRanking", pos.load()}
                });
        }
        case GameState::ENDED: {
            return sv->Send(fd, {
                    {"type", Request::GAME_ENDED},
                    {"score", score.load()},
                    {"placeInRanking", pos.load()}
            });
        }
        case GameState::SHUT: {
            return sv->Send(fd, {
                    {"type", Request::GAME_SHUTDOWN},
                    {"score", score.load()},
                    {"placeInRanking", pos.load()}
            });
        }
        default: return;
    }
}
