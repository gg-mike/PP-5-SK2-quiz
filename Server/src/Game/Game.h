#pragma once

#include "Game/Participants/Host.h"
#include "Game/Participants/Player.h"

class Game {
private:
    std::atomic<bool> running;
    std::atomic<bool> isRound;
    std::atomic<int> gameCode;
    int currentQuestion{0};


    Host host;
    std::vector<Player> players;

public:




    [[nodiscard]] bool GetRunning() const { return running; }
    [[nodiscard]] bool GetIsRound() const { return isRound; }


};
