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

    nlohmann::json PlayerAnswered(int gameCode, const std::string& nick, const nlohmann::json& answerJson);
    nlohmann::json PlayerExit(int gameCode, const std::string& nick);

    void JoinGame(int gameCode, const std::shared_ptr<Player>& player);

    [[nodiscard]] bool GameExists(int gameCode) const;
    [[nodiscard]] bool NickFree(int gameCode, const std::string& nick) const;

    Enumerators::GameState GetState(int gameCode);

private:
    int GenerateGameCode();

};


