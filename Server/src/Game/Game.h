#pragma once

#include <shared_mutex>

#include "nlohmann/json.hpp"

#include "Game/Participants/Host.h"
#include "Game/Participants/Player.h"

#include "../Enumerators/GameState.h"

class Game {
public:
    struct Question {
        std::string content;
        std::array<std::string, 4> answers;
        std::string rightAnswer;
    };

private:
    std::atomic<Enumerators::GameState> state{Enumerators::CREATED};
    std::atomic<bool> canAnswer{false};
    int currentQuestion{0};

    std::mutex mtx;
    std::unique_lock<std::mutex> lock;

    std::shared_ptr<Host> host;
    std::map<std::string, std::shared_ptr<Player>> players{};
    std::map<std::string, std::string> answers{};

    std::map<int, Question> questions;

public:
    explicit Game(std::shared_ptr<Host> host);
    Game& Lock();
    void Unlock();

    nlohmann::json AddQuestions(const nlohmann::json& questionsJson);
    nlohmann::json Start();
    nlohmann::json End();
    nlohmann::json StartRound();
    nlohmann::json EndRound();
    nlohmann::json HostExit();

    nlohmann::json PlayerAnswered(const std::string& nick, const nlohmann::json &answerJson);
    nlohmann::json PlayerExit(const std::string &nick);

    [[nodiscard]] bool IsOpened() const { return state == Enumerators::OPENED; }
    [[nodiscard]] Enumerators::GameState GetState() const { return state; }

    [[nodiscard]] const std::map<std::string, std::shared_ptr<Player>>& GetPlayers() const { return players; }
    void AddPlayer(const std::shared_ptr<Player>& player);

private:
    nlohmann::json GenerateRanking();
    void AllAnsweredCheck();
    void NotifyPlayers() const;
    [[nodiscard]] static nlohmann::json GenerateUnexpectedRequest();
};
