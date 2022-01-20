#pragma once

#include "IGameParticipant.h"

class Host : public IGameParticipant {
public:
    nlohmann::json Process(const nlohmann::json& request) override;
};
