#pragma once

#include "../Game/Participants/IGameParticipant.h"

class Client {
private:
    int maxTimeBetweenHb_ms;
    std::atomic<bool> running{true};
    std::atomic<int> fd;
    std::atomic<std::chrono::high_resolution_clock::time_point> lastHeartbeat;

    std::thread heartbeatThread;
    std::thread readerThread;

    std::shared_ptr<IGameParticipant> gameParticipant{nullptr};

public:
    explicit Client(int fd);
    void Shutdown();

private:
    void CheckHeartbeat();
    void ReadMessages();
    void ProcessRequest(const nlohmann::json& request);

    void SendShutdownMessage() const;
};
