#include "pch.h"

#include "Object/Server.h"


int main(int argc, char ** argv) {
    if (argc < 2) {
        LOGERROR("Argument [port] is missing");
        exit(EXIT_FAILURE);
    }

    Server::GetInstance()->Create(argv[1]);
    Server::GetInstance()->Listen(32);
    Server::GetInstance()->Poll();

    return 0;
}