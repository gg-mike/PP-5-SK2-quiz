#pragma once

class Server {
private:
    int fd = -1;

public:
    static std::shared_ptr<Server> GetInstance();

    ~Server();

    void Create(char* port);
    void Listen(int conn_num);
    int Accept();

    int GetServerFD() const { return fd; }
};
