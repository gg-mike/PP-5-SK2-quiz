#include "pch.h"
#include "Player.h"
#include "Enumerators/Request.h"

using nlohmann::json;
using Enumerators::Request;

Player::Player(int fd, int gameCode, const std::string& nick)
    : fd(fd), gameCode(gameCode), nick(nick.c_str()) {}

json Player::Process(Request type, const json& request) {
    return {{"type", Request::ACCEPT}};
}
