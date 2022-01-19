#pragma once

class Client {
private:
    int maxTimeBetweenHb_ms;
    std::atomic<bool> running{true};
    std::atomic<int> fd;
    std::atomic<std::chrono::high_resolution_clock::time_point> lastHeartbeat;

    std::thread heartbeatThread;
    std::thread readerThread;

public:
    explicit Client(int fd);
    void Shutdown();

private:
    void CheckHeartbeat();
    void ReadMessages();
    void ProcessMessage(const std::string& message) const;

    void SendShutdownMessage() const;
};
