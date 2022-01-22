#pragma once

#include "IGameParticipant.h"

class Host : public IGameParticipant {
private:
    std::atomic<int> fd;
    std::atomic<bool>& stateChange;
    std::atomic<int> gameCode{};

public:
    Host(int fd, std::atomic<bool>& stateChange);
    nlohmann::json Process(Enumerators::Request type, const nlohmann::json& request) override;
    [[nodiscard]] int GetType() const override { return 1; }

    [[nodiscard]] int GetFd() const { return fd; }

    void SetStateChange(bool _stateChange) { stateChange = _stateChange; }

    void SetGameCode(int _gameCode) { gameCode = _gameCode; }
    [[nodiscard]] int GetGameCode() const { return gameCode; }
};
