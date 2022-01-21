#pragma once

#include <shared_mutex>

#include "nlohmann/json.hpp"

#include "Game/Participants/Host.h"
#include "Game/Participants/Player.h"

class Game {
public:
    enum State {
        CREATED,
        OPENED,
        STARTED,
        ROUND_STARTED,
        ROUND_ENDED,
        ENDED
    };

    struct Question {
        std::string content;
        std::array<std::string, 4> answers;
        std::string rightAnswer;
    };

private:
    std::atomic<State> state{CREATED};
    int currentQuestion{0};

    std::shared_mutex playersMtx;
    std::condition_variable playersCv;

    std::mutex mtx;
    std::unique_lock<std::mutex> lock;

    std::shared_ptr<Host> host;
    std::vector<std::shared_ptr<Player>> players{};

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

    [[nodiscard]] bool IsOpened() const { return state == OPENED; }
    [[nodiscard]] bool GetState() const { return state; }

    [[nodiscard]] const std::vector<std::shared_ptr<Player>>& GetPlayers() const { return players; }
    void AddPlayer(const std::shared_ptr<Player>& player);
    void RemovePlayer(const std::shared_ptr<Player>& player);

private:
    [[nodiscard]] static nlohmann::json GenerateUnexpectedRequest() ;
};
