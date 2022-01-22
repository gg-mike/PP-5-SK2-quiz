#include "pch.h"
#include "Host.h"

#include "Enumerators/Request.h"
#include "Server/Database.h"

using nlohmann::json;
using Enumerators::Request;

Host::Host(int fd, std::atomic<bool>& stateChange)
    : fd(fd), stateChange(stateChange) {}

json Host::Process(Request type, const json& request) {
    auto db = Database::GetInstance();
    switch (type) {
        case Request::ACCEPT:
        case Request::DECLINE:
        case Request::ERROR:
        case Request::NONE:             return {};
        case Request::EXIT_GAME:        return db->HostExit(gameCode);
        case Request::ADD_QUESTIONS:    return db->AddQuestions(gameCode, request);
        case Request::START_GAME:       return db->StartGame(gameCode);
        case Request::END_GAME:         return db->EndGame(gameCode);
        case Request::START_ROUND:      return db->StartRound(gameCode);
        case Request::END_ROUND:        return db->EndRound(gameCode);
        default: return {
            {"type", Request::ERROR},
            {"desc", "Unexpected request type"}
        };
    }
}
