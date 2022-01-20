#pragma once
#include "nlohmann/json.hpp"

class IGameParticipant {
public:
    virtual nlohmann::json Process(const nlohmann::json& request) = 0;
};
