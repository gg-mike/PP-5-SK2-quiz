#include "pch.h"

#include "util.h"
#include "Server.h"

int main(int argc, char ** argv) {
    util::check(argc >= 2, "argument [port] is missing");

    char buffer[util::BUFFER_SIZE];
    ssize_t bytesRead;
    int clientFD;

    Server::GetInstance()->Create(argv[1]);
    Server::GetInstance()->Listen(1);

    clientFD = Server::GetInstance()->Accept();
    {
        do {
            bytesRead = util::readData(clientFD, buffer, util::BUFFER_SIZE);
            if (bytesRead > 0)
                std::cout << "(" << clientFD << "): " << buffer;
            memset(buffer, 0, sizeof buffer);
        } while (bytesRead != -1);
    }
    shutdown(clientFD, SHUT_RDWR);
    close(clientFD);

    return 0;
}