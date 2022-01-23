#include "pch.h"
#include "Game.h"

#include "Enumerators/Request.h"

using nlohmann::json;
using Json::Field, Json::Test;
using Enumerators::GameState, Enumerators::Request;

Game::Game(std::shared_ptr<Host> host)
    : host(std::move(host)) {
    lock = std::unique_lock<std::mutex>{mtx};
    lock.unlock();
    LOGINFO("Host (fd=", this->host->GetFd(), ") created game");
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

    std::vector<std::string> fieldNames{"Q", "A", "B", "C", "D", "rightAnswer"};
    bool done;

    for (const auto& obj: array.value) {
        done = false;

        Field<int> n = Test<int>(obj, "n", hostFd, Request::ADD_QUESTIONS);
        if (n.opStatus != 1) break;

        std::vector<Field<std::string>> fields;

        for (const auto& fieldName: fieldNames) {
            Field<std::string> f = Test<std::string>(obj, fieldName, hostFd, Request::ADD_QUESTIONS);
            if (f.opStatus != 1) break;
            fields.push_back(f);
        }

        if (fields.size() != fieldNames.size()) break;

        availableQuestions.insert(n.value);
        currentQuestion = *availableQuestions.begin();

        questions.insert({n.value, {
            fields[0].value,
            {
                fields[1].value,
                fields[2].value,
                fields[3].value,
                fields[4].value
                },
            fields[5].value}});

        done = true;
    }

    if (done) {
        LOGINFO("Host (game=", host->GetGameCode(), ") added questions");
        state = GameState::OPENED;
        return {
                {"type", Request::ADD_QUESTIONS | Request::ACCEPT},
                {"questionsNumber", questions.size()}
        };
    }
    LOGWARNING("Host (game=", host->GetGameCode(), ") could not add questions");
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

    LOGINFO("Host (game=", host->GetGameCode(), ") started game");
    return {{"type", Request::START_GAME | Request::ACCEPT}};
}

json Game::End() {
    if (state == GameState::CREATED || state == GameState::R_STARTED)
        return Game::GenerateUnexpectedRequest();

    state = GameState::ENDED;
    NotifyPlayers();

    LOGINFO("Host (game=", host->GetGameCode(), ") ended game");
    return {{"type", Request::END_GAME | Request::ACCEPT}};
}

json Game::StartRound() {
    if (state != GameState::STARTED && state != GameState::R_ENDED)
        return Game::GenerateUnexpectedRequest();
    else if (availableQuestions.empty())
        return {
            {"type", Request::START_ROUND | Request::DECLINE},
            {"desc", "No more questions left"}
        };

    state = GameState::R_STARTED;
    canAnswer = true;
    answersNumberThread = std::thread(&Game::AnswersNumberHandler, this);
    {
        std::lock_guard _{answersMtx};
        answers.clear();
    }
    NotifyPlayers();

    Question currQ = questions.at(currentQuestion);

    LOGINFO("Host (game=", host->GetGameCode(), ") started round");
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
    answersNumberThread.join();

    json response = GenerateRanking();
    NotifyPlayers();
    response["type"] = Request::END_ROUND | Request::ACCEPT;
    availableQuestions.erase(currentQuestion);
    if (!availableQuestions.empty())
        currentQuestion = *availableQuestions.begin();
    LOGINFO("Host (game=", host->GetGameCode(), ") ended round");
    return response;
}

nlohmann::json Game::HostExit() {
    state = GameState::SHUT;
    NotifyPlayers();

    host->SetStateChange(true);
    LOGINFO("Host (game=", host->GetGameCode(), ") exited");
    return {{"type", Request::EXIT_GAME | Request::ACCEPT}};
}

nlohmann::json Game::PlayerAnswered(const std::string &nick, const json &answerJson) {
    if (!canAnswer) return {{"type", Request::ANSWER | Request::DECLINE}};

    Field<std::string> answer = Test<std::string>(answerJson, "answer", players.at(nick)->GetFd(), Request::ANSWER);
    if (answer.opStatus != 1) return {};

    {
        std::lock_guard _{answersMtx};
        if (std::string("ABCD").find(answer.value) == std::string::npos)
            answers.insert({nick, ""});
        else
            answers.insert({nick, answer.value});
    }
    players.at(nick)->SetAnswered(true);
    players.at(nick)->SetWasCorrectAnswer(answer.value == questions.at(currentQuestion).rightAnswer);

    AllAnsweredCheck();

    LOGINFO("Player (game=", host->GetGameCode(), ", nick=", nick, ") answered");
    return {{"type", Request::ANSWER | Request::ACCEPT}};
}

nlohmann::json Game::PlayerExit(const std::string &nick) {
    players.at(nick)->SetStateChange(true);

    players.erase(nick);
    {
        std::lock_guard _{answersMtx};
        answers.erase(nick);
    }

    AllAnsweredCheck();

    LOGINFO("Player (game=", host->GetGameCode(), ", nick=", nick, ") exited");
    return {{"type", Request::EXIT_GAME | Request::ACCEPT}};
}

void Game::PlayerJoined(const std::shared_ptr<Player>& player) {
    if (state != GameState::OPENED) {
        Server::GetInstance()->Send(host->GetFd(), Game::GenerateUnexpectedRequest());
        return;
    }

    players.insert({player->GetNick(), player});

    LOGINFO("Player (game=", host->GetGameCode(), ", nick=", player->GetNick(), ") joined");
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

    {
        std::lock_guard _{answersMtx};
        for (const auto&[nick, player]: players) {
            if (answers.contains(nick)) {
                if (!answers.at(nick).empty())
                    j["results"][answers.at(nick)] = (int) j["results"][answers.at(nick)] + 1;
                if (players.at(nick)->GetWasCorrectAnswer())
                    players.at(nick)->IncreaseScore(1);
            } else
                answers.insert({nick, ""});
        }
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
        if (prevPos > 3) break;
        j["ranking"] += {
            {"nick", player->GetNick()},
            {"pos", player->GetPos()},
            {"score", player->GetScore()}
        };
    }
    return j;
}

void Game::AllAnsweredCheck() {
    unsigned long answersSize;
    {
        std::lock_guard _{answersMtx};
        answersSize = answers.size();
    }
    if (state == GameState::R_STARTED && players.size() == answersSize) {
        Server::GetInstance()->Send(host->GetFd(), {{"type", Request::ALL_ANSWERED}});
        canAnswer = false;
    }
}

void Game::AnswersNumberHandler() {
    int t = Server::GetInstance()->GetConfig().timeBetweenAnswersNumberSend_sec;
    unsigned long answersSize;
    while(canAnswer) {
        std::this_thread::sleep_for(std::chrono::seconds (t));
        {
            std::lock_guard _{answersMtx};
            answersSize = answers.size();
        }
        Server::GetInstance()->Send(host->GetFd(), {
                {"type", Request::CURRENT_RESULTS},
                {"answers", answersSize}
        });
    }
}

void Game::NotifyPlayers() const {
    for (auto& [nick, player]: players)
        player->RequestHandler(state);
}

json Game::GenerateUnexpectedRequest() {
    return {
        {"type", Request::ERROR},
        {"desc", "Unexpected request type"}
    };
}
