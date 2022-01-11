#pragma once

class Server {
private:
    int fd{-1};
    ssize_t buffer_size{};
    bool running{false};

public:
    static std::shared_ptr<Server> GetInstance();

    ~Server();

    void Create(char* _port, ssize_t _buffer_size=256);
    void Listen(int conn_num) const;
    int Accept() const;
    void Poll();
    ssize_t Read(int _fd, std::string& message) const;
    ssize_t Recv(int _fd, std::string &message) const;

    void Write(int _fd, const std::string& message) const;
public:

    friend void signal_callback_handler(int signal);
};
