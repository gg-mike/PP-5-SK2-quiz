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

    std::mutex mtx;
    std::unique_lock<std::mutex> lock;
    std::thread answersNumberThread;

    std::shared_ptr<Host> host;
    std::map<std::string, std::shared_ptr<Player>> players{};
    std::map<std::string, std::string> answers{};

    unsigned int currentQuestion{0};
    std::set<unsigned int> availableQuestions;
    std::map<unsigned int, Question> questions;

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

    void PlayerJoined(const std::shared_ptr<Player>& player);
    nlohmann::json PlayerAnswered(const std::string& nick, const nlohmann::json &answerJson);
    nlohmann::json PlayerExit(const std::string &nick);

    [[nodiscard]] Enumerators::GameState GetState() const { return state; }
    [[nodiscard]] const std::map<std::string, std::shared_ptr<Player>>& GetPlayers() const { return players; }

private:
    void AllAnsweredCheck();

    void NotifyPlayers() const;

    void AnswersNumberHandler();

    nlohmann::json GenerateRanking();
    static nlohmann::json GenerateUnexpectedRequest();
};
