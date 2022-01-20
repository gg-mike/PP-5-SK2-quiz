#pragma once

#include "IGameParticipant.h"

class Player : public IGameParticipant {
private:
    int score{0};
    std::string nick{};

public:
    nlohmann::json Process(const nlohmann::json& request) override;

};
