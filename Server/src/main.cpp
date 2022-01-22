#include "pch.h"

#include "Server/Server.h"


int main(int, char **) {
    Server::GetInstance()->Run();
    Server::GetInstance()->Shutdown();
    return 0;
}