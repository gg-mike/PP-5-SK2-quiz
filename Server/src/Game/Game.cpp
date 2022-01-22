#include "pch.h"
#include "Game.h"

#include <utility>

#include "Enumerators/Request.h"

using nlohmann::json;
using Json::Field, Json::Test;
using Enumerators::GameState, Enumerators::Request;

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
    if (state != GameState::CREATED)
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
        state = GameState::OPENED;
        return {
                {"type", Request::ADD_QUESTIONS | Request::ACCEPT},
                {"questionsNumber", questions.size()}
        };
    }
    return {};
}

json Game::Start() {
    if (state != GameState::OPENED)
        return Game::GenerateUnexpectedRequest();
    else if (players.empty())
        return {
                {"type", Request::START_GAME | Request::DECLINE},
                {"desc", "No player joined the game yet"}
        };

    state = GameState::STARTED;
    NotifyPlayers();

    return {{"type", Request::START_GAME | Request::ACCEPT}};
}

json Game::End() {
    if (state == GameState::CREATED || state == GameState::R_STARTED)
        return Game::GenerateUnexpectedRequest();

    state = GameState::ENDED;
    NotifyPlayers();

    return {{"type", Request::END_GAME | Request::ACCEPT}};
}

json Game::StartRound() {
    if (state != GameState::STARTED && state != GameState::R_ENDED)
        return Game::GenerateUnexpectedRequest();
    else if (currentQuestion >= questions.size())
        return {
            {"type", Request::START_ROUND | Request::DECLINE},
            {"desc", "No more questions left"}
        };

    state = GameState::R_STARTED;
    canAnswer = true;
    answers.clear();
    NotifyPlayers();

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
    if (state != GameState::R_STARTED)
        return {
                {"type", Request::END_ROUND | Request::DECLINE},
                {"desc", "Round haven't been started"}
        };

    state = GameState::R_ENDED;
    canAnswer = false;

    json response = GenerateRanking();
    NotifyPlayers();
    response["type"] = Request::END_ROUND | Request::ACCEPT;
    currentQuestion++;
    return response;
}

nlohmann::json Game::HostExit() {
    state = GameState::SHUT;
    NotifyPlayers();

    host->SetStateChange(true);
    return {{"type", Request::EXIT_GAME | Request::ACCEPT}};
}

nlohmann::json Game::PlayerAnswered(const std::string &nick, const json &answerJson) {
    if (!canAnswer) return {{"type", Request::ANSWER | Request::DECLINE}};

    Field<std::string> answer = Test<std::string>(answerJson, "answer", players.at(nick)->GetFd(), Request::ANSWER);
    if (answer.opStatus != 1) return {};

    if (std::string("ABCD").find(answer.value) == std::string::npos)
        answers.insert({nick, ""});
    else
        answers.insert({nick, answer.value});

    players.at(nick)->SetAnswered(true);
    players.at(nick)->SetWasCorrectAnswer(answer.value == questions.at(currentQuestion).rightAnswer);

    AllAnsweredCheck();

    return {{"type", Request::ANSWER | Request::ACCEPT}};
}

nlohmann::json Game::PlayerExit(const std::string &nick) {
    players.at(nick)->SetStateChange(true);

    players.erase(nick);
    answers.erase(nick);

    AllAnsweredCheck();

    return {{"type", Request::EXIT_GAME | Request::ACCEPT}};
}

void Game::AddPlayer(const std::shared_ptr<Player>& player) {
    if (state != GameState::OPENED) {
        Server::GetInstance()->Send(host->GetFd(), Game::GenerateUnexpectedRequest());
        return;
    }

    players.insert({player->GetNick(), player});

    Server::GetInstance()->Send(host->GetFd(), {
        {"type", Request::PLAYER_JOINED},
        {"nick", player->GetNick()}
    });
}

json Game::GenerateRanking() {
    json j{
        {"results", {{"A", 0}, {"B", 0}, {"C", 0}, {"D", 0}}},
        {"ranking", {}}
    };

    for (const auto& [nick, player]: players) {
        if (answers.contains(nick)) {
            if (!answers.at(nick).empty())
                j["results"][answers.at(nick)] = (int)j["results"][answers.at(nick)] + 1;
            if (players.at(nick)->GetWasCorrectAnswer())
                players.at(nick)->IncreaseScore(1);
        }
        else
            answers.insert({nick, ""});
    }

    std::vector<std::shared_ptr<Player>> tmp{};
    for (const auto& player: players)
        tmp.push_back(player.second);

    using T = const std::shared_ptr<Player>&;
    std::sort(tmp.begin(), tmp.end(), [](T a, T b) {
        return a->GetScore() < b->GetScore();});
    std::reverse(tmp.begin(), tmp.end());

    tmp.front()->SetPos(1);
    int prevPos = tmp.front()->GetPos();
    int prevScore = tmp.front()->GetScore();
    for (auto& player: tmp) {
        if (prevScore == player->GetScore()) player->SetPos(prevPos);
        else {
            player->SetPos(++prevPos);
            prevScore = player->GetScore();
        }
        j["ranking"] += {
            {"nick", player->GetNick()},
            {"pos", player->GetPos()},
            {"score", player->GetScore()}
        };
    }
    return j;
}

void Game::AllAnsweredCheck() {
    if (state == GameState::R_STARTED && players.size() == answers.size()) {
        Server::GetInstance()->Send(host->GetFd(), {{"type", Request::ALL_ANSWERED}});
        canAnswer = false;
    }
}

void Game::NotifyPlayers() const {
    for (auto& [nick, player]: players)
        player->RequestHandler(state);
}

json Game::GenerateUnexpectedRequest() {
    return {
        {"type", Request::ERROR},
        {"typeText", "ERROR"},
        {"desc", "Unexpected request type"}
    };
}
