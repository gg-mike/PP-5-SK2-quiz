#pragma once

#include "IGameParticipant.h"

class Host : public IGameParticipant {
private:
    std::atomic<int> fd;
    std::atomic<int> gameCode{};

public:
    explicit Host(int fd);
    nlohmann::json Process(Enumerators::Request type, const nlohmann::json& request) override;

    [[nodiscard]] int GetFd() const { return fd; }

    void SetGameCode(int _gameCode) { gameCode = _gameCode; }
    [[nodiscard]] int GetGameCode() const { return gameCode; }
};
