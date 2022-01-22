#pragma once

#include "../Game/Participants/IGameParticipant.h"

class Client {
private:
    // From server config
    std::atomic<int> maxTimeBetweenHb_ms{};
    std::string messageBegin;
    std::string messageEnd;

    std::atomic<bool> running{true};
    std::atomic<int> fd;
    std::atomic<std::chrono::high_resolution_clock::time_point> lastHeartbeat;

    std::thread heartbeatThread;
    std::thread messageThread;
    std::atomic<bool> gameParticipantStateChange{false};
    std::shared_ptr<IGameParticipant> gameParticipant{nullptr};

public:
    explicit Client(int fd);
    void Shutdown();
    nlohmann::json ExitFromGame();

private:
    void HeartbeatHandler();
    void MessageHandler();
    void ProcessRequest(const nlohmann::json& request);

    void SendShutdownMessage() const;
};
