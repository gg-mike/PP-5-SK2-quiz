#pragma once

class Server {
public:
    static constexpr int USE_CONFIG{-1};
    static constexpr const char * DEFAULT_CONFIG{"res/defaultConfig.json"};

    enum State {
        None =      0,
        Inited =    1 << 2,
        Listening = 1 << 3,
        Running =   1 << 4,
    };

    struct Config {
        int port;
        ssize_t bufSize;
        int listenConnectionNumber;
    };

private:
    int state{None};
    int fd{-1};
    Config config{};

public:
    static std::shared_ptr<Server> GetInstance();

    ~Server();

    void Run(const std::string& configFile=DEFAULT_CONFIG);
    void Init(const std::string &configFile=DEFAULT_CONFIG);
    void Listen(int conn_num=USE_CONFIG);
    int Accept() const;
    void Poll();
    ssize_t Read(int _fd, std::string& message) const;
    ssize_t Recv(int _fd, std::string &message) const;

    void Write(int _fd, const std::string& message) const;

public:
    friend void signal_callback_handler(int signal);

private:
    void ReadConfig(const std::string& configFile);
};
