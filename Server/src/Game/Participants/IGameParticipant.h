#pragma once
#include "nlohmann/json.hpp"
#include "../../Enumerators/Request.h"

class IGameParticipant {
public:
    virtual nlohmann::json Process(Enumerators::Request type, const nlohmann::json& request) = 0;
    [[nodiscard]] virtual int GetType() const = 0;
};
