#pragma once

#include "Game/Game.h"

class Database {
private:
    std::map<int, std::shared_ptr<Game>> games{};

    mutable std::shared_mutex mtx;

public:
    static std::shared_ptr<Database> GetInstance();

    int CreateNewGame(const std::shared_ptr<Host>& host);
    nlohmann::json AddQuestions(int gameCode, const nlohmann::json& questionsJson);
    nlohmann::json StartGame(int gameCode);
    nlohmann::json EndGame(int gameCode);
    nlohmann::json StartRound(int gameCode);
    nlohmann::json EndRound(int gameCode);
    nlohmann::json HostExit(int gameCode);

    void JoinGame(int gameCode, const std::shared_ptr<Player>& player);

    [[nodiscard]] bool GameExists(int gameCode) const;
    [[nodiscard]] bool NickFree(int gameCode, const std::string& nick) const;

private:
    int GenerateGameCode();

};


