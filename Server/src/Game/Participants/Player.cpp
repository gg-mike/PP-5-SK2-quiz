#include "pch.h"
#include "Player.h"

#include "Enumerators/Request.h"
#include "Object/Database.h"

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
    switch (currGameState) {
        case GameState::STARTED: {
            Server::GetInstance()->Send(fd, {
                    {"type", Request::GAME_STARTED},
                    {"typeText", "GAME_STARTED"}
            });
            break;
        }
        case GameState::R_STARTED: {
            answered = false;
            wasCorrectAnswer = false;
            Server::GetInstance()->Send(fd, {
                    {"type", Request::ROUND_STARTED},
                    {"typeText", "ROUND_STARTED"}
            });
            break;
        }
        case GameState::R_ENDED: {
            if (answered)
                Server::GetInstance()->Send(fd, {
                        {"type", Request::ROUND_ENDED},
                        {"typeText", "ROUND_ENDED"},
                        {"wasCorrectAnswer", wasCorrectAnswer.load()},
                        {"score", score.load()},
                        {"placeInRanking", pos.load()}
                });
            else
                Server::GetInstance()->Send(fd, {
                        {"type", Request::ROUND_TIMEOUT},
                        {"typeText", "ROUND_TIMEOUT"},
                        {"wasCorrectAnswer", false},
                        {"score", score.load()},
                        {"placeInRanking", pos.load()}
                });
            break;
        }
        case GameState::ENDED: {
            Server::GetInstance()->Send(fd, {
                    {"type", Request::GAME_ENDED},
                    {"typeText", "GAME_ENDED"},
                    {"score", score.load()},
                    {"placeInRanking", pos.load()}
            });
            break;
        }
        case GameState::SHUT: {
            Server::GetInstance()->Send(fd, {
                    {"type", Request::GAME_SHUTDOWN},
                    {"typeText", "GAME_SHUTDOWN"},
                    {"score", score.load()},
                    {"placeInRanking", pos.load()}
            });
            break;
        }
        default: ;
    }
}
