#include "pch.h"
#include "Database.h"

using nlohmann::json;

std::mt19937& GetEngine() {
    static std::random_device rd;
    static std::mt19937 mt(rd());
    return mt;
}

std::uniform_int_distribution<int> GetGameCodeGen() {
    static std::uniform_int_distribution<int> gameCodeGen(100000, 999999);
    return gameCodeGen;
}

std::shared_ptr<Database> Database::GetInstance() {
    static const std::shared_ptr<Database> DatabaseInstance(new Database());
    return DatabaseInstance;
}

int Database::CreateNewGame(const std::shared_ptr<Host>& host) {
    std::lock_guard _{mtx};
    int gameCode = GenerateGameCode();
    games.insert({gameCode, std::make_shared<Game>(host)});
    return gameCode;
}

json Database::AddQuestions(int gameCode, const nlohmann::json& questionsJson) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.AddQuestions(questionsJson);
    game.Unlock();
    return response;
}

json Database::StartGame(int gameCode) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.Start();
    game.Unlock();
    return response;
}

json Database::EndGame(int gameCode) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.End();
    game.Unlock();
    return response;
}

json Database::StartRound(int gameCode) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.StartRound();
    game.Unlock();
    return response;
}

json Database::EndRound(int gameCode) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.EndRound();
    game.Unlock();
    return response;
}

json Database::HostExit(int gameCode) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.HostExit();
    game.Unlock();
    return response;
}

json Database::PlayerAnswered(int gameCode, const std::string& nick, const json &answerJson) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.PlayerAnswered(nick, answerJson);
    game.Unlock();
    return response;
}

json Database::PlayerExit(int gameCode, const std::string& nick) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    json response = game.PlayerExit(nick);
    game.Unlock();
    return response;
}

void Database::JoinGame(int gameCode, const std::shared_ptr<Player>& player) {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();
    game.AddPlayer(player);
    game.Unlock();
}

bool Database::GameExists(int gameCode) const {
    std::shared_lock _{mtx};
    if (games.contains(gameCode)) {
        Game& game = games.at(gameCode)->Lock();
        bool exists = game.IsOpened();
        game.Unlock();
        return exists;
    }
    else return false;
}

bool Database::NickFree(int gameCode, const std::string& nick) const {
    std::shared_lock _{mtx};
    Game& game = games.at(gameCode)->Lock();

    bool free = true;
    for (const auto& [playerNick, p]: game.GetPlayers()) {
        if (nick == playerNick) {
            free = false;
            break;
        }
    }

    game.Unlock();
    return free;
}

Enumerators::GameState Database::GetState(int gameCode) {
    std::shared_lock _{mtx};
    return games.at(gameCode)->GetState();
}

int Database::GenerateGameCode() {
    int gameCode;
    do {
        gameCode = GetGameCodeGen()(GetEngine());
    } while (games.contains(gameCode));
    return gameCode;
}
