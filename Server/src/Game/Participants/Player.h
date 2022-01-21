#pragma once

#include "IGameParticipant.h"

class Player : public IGameParticipant {
private:
    std::atomic<int> fd;
    std::atomic<int> score{0};
    std::atomic<int> gameCode;
    std::atomic<const char*> nick;

public:
    Player(int fd, int gameCode, const std::string& nick);
    nlohmann::json Process(Enumerators::Request type, const nlohmann::json& request) override;

    [[nodiscard]] int GetFd() const { return fd; }
    [[nodiscard]] int GetScore() const { return score; }
    [[nodiscard]] int GetGameCode() const { return gameCode; }
    // TODO: Nick encoding
    [[nodiscard]] std::string GetNick() const { return {nick}; }
};
