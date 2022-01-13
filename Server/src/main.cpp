#include "pch.h"

#include "Object/Server.h"


int main(int, char **) {
    Server::GetInstance()->Run();
    return 0;
}