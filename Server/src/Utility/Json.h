#pragma once
#include "nlohmann/json.hpp"
#include "../Enumerators/Request.h"
#include "../Object/Server.h"

namespace Json {

    template<typename T>
    struct Field {
        short opStatus{};
        std::string name{};
        T value{};
    };

    template<typename T>
    Field<T> Get(const nlohmann::json& json, const std::string& fieldName);

    template<typename T>
    bool GenerateResponse(const Field<T>& field, nlohmann::json& response, Enumerators::RequestCode errorCode);

    template<typename T>
    Field<T> Test(const nlohmann::json& json, const std::string& fieldName, int fd, Enumerators::RequestCode requestCode) {
        Field<T> field = Get<T>(json, fieldName);
        nlohmann::json response;
        if (Json::GenerateResponse<>(field, response, requestCode | Enumerators::Request::ERROR))
            Server::GetInstance()->Send(fd, response);
        return field;
    }

}
