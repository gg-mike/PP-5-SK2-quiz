#include "pch.h"
#include "Game.h"

#include <utility>

#include "Enumerators/Request.h"

using nlohmann::json;
using Json::Field, Json::Test;
using Enumerators::Request;

Game::Game(std::shared_ptr<Host> host)
    : host(std::move(host)) {
    lock = std::unique_lock<std::mutex>{mtx};
    lock.unlock();
}

Game &Game::Lock() {
    lock.lock();
    return *this;
}

void Game::Unlock() {
    lock.unlock();
}

json Game::AddQuestions(const json &questionsJson) {
    int hostFd = host->GetFd();
    if (state != CREATED)
        return Game::GenerateUnexpectedRequest();

    Field<json::array_t> array = Test<json::array_t>(questionsJson, "questions", hostFd, Request::ADD_QUESTIONS);
    if (array.opStatus != 1) return {};

    bool done;

    for (const auto& obj: array.value) {
        done = false;

        Field<int> n = Test<int>(obj, "n", hostFd, Request::ADD_QUESTIONS);
        if (n.opStatus != 1) break;

        Field<std::string> q = Test<std::string>(obj, "Q", hostFd, Request::ADD_QUESTIONS);
        if (q.opStatus != 1) break;

        Field<std::string> a = Test<std::string>(obj, "A", hostFd, Request::ADD_QUESTIONS);
        if (a.opStatus != 1) break;

        Field<std::string> b = Test<std::string>(obj, "B", hostFd, Request::ADD_QUESTIONS);
        if (b.opStatus != 1) break;

        Field<std::string> c = Test<std::string>(obj, "C", hostFd, Request::ADD_QUESTIONS);
        if (c.opStatus != 1) break;

        Field<std::string> d = Test<std::string>(obj, "D", hostFd, Request::ADD_QUESTIONS);
        if (d.opStatus != 1) break;

        Field<std::string> rightAnswer = Test<std::string>(obj, "rightAnswer", hostFd, Request::ADD_QUESTIONS);
        if (rightAnswer.opStatus != 1) break;

        questions.insert({n.value, {
            q.value,
            {
                a.value,
                b.value,
                c.value,
                d.value
                },
            rightAnswer.value}});

        done = true;
    }

    if (done) {
        state = OPENED;
        return {
                {"type", Request::ADD_QUESTIONS | Request::ACCEPT},
                {"questionsNumber", questions.size()}
        };
    }
    return {};
}

json Game::Start() {
    if (state != OPENED)
        return Game::GenerateUnexpectedRequest();
    else if (players.empty())
        return {
                {"type", Request::START_GAME | Request::DECLINE},
                {"desc", "No player joined the game yet"}
        };

    state = STARTED;
    // TODO: Notify players

    return {{"type", Request::START_GAME | Request::ACCEPT}};
}

json Game::End() {
    if (state == CREATED || state == ROUND_STARTED)
        return Game::GenerateUnexpectedRequest();

    state = ENDED;
    // TODO: Notify players

    return {{"type", Request::END_GAME | Request::ACCEPT}};
}

json Game::StartRound() {
    if (state != STARTED && state != ROUND_ENDED)
        return Game::GenerateUnexpectedRequest();
    else if (currentQuestion >= questions.size())
        return {
            {"type", Request::START_ROUND | Request::DECLINE},
            {"desc", "No more questions left"}
        };

    state = ROUND_STARTED;
    // TODO: Notify players

    Question currQ = questions.at(currentQuestion);

    return {
        {"type", Request::START_ROUND | Request::ACCEPT},
        {"question", {
            {"n", currentQuestion},
            {"Q", currQ.content},
            {"A", currQ.answers[0]},
            {"B", currQ.answers[1]},
            {"C", currQ.answers[2]},
            {"D", currQ.answers[3]},
            {"rightAnswer", currQ.rightAnswer}
        }}
    };
}

json Game::EndRound() {
    if (state != ROUND_STARTED)
        return {
                {"type", Request::END_ROUND | Request::DECLINE},
                {"desc", "Round haven't been started"}
        };

    state = ROUND_ENDED;
    // TODO: Notify players
    currentQuestion++;

    return {{"type", Request::END_ROUND | Request::ACCEPT}};
}

nlohmann::json Game::HostExit() {
    state = ENDED;
    // TODO: Notify players

    return {{"type", Request::EXIT_GAME | Request::ACCEPT}};
}

void Game::AddPlayer(const std::shared_ptr<Player>& player) {
    if (state != OPENED) {
        Server::GetInstance()->Send(host->GetFd(), Game::GenerateUnexpectedRequest());
        return;
    }

    players.push_back(player);

    Server::GetInstance()->Send(host->GetFd(), {
        {"type", Request::PLAYER_JOINED},
        {"nick", player->GetNick()}
    });
}

void Game::RemovePlayer(const std::shared_ptr<Player>& player) {
    players.erase(std::find(players.begin(), players.end(), player));
}

json Game::GenerateUnexpectedRequest() {
    return {
        {"type", Request::ERROR},
        {"desc", "Unexpected request type"}
    };
}
