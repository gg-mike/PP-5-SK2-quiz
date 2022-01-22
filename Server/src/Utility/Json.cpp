#include "pch.h"
#include "Json.h"

namespace Json {

    template<>
    Field<int> Get(const nlohmann::json& json, const std::string& fieldName) {
        if (!json.contains(fieldName))
            return {-1, fieldName, 0};

        if (!json[fieldName].is_number_integer())
            return {0, fieldName, 0};

        return {1, fieldName, json[fieldName]};
    }

    template<>
    Field<std::string> Get(const nlohmann::json& json, const std::string& fieldName) {
        if (!json.contains(fieldName))
            return {-1, fieldName, ""};

        if (!json[fieldName].is_string())
            return {0, fieldName, ""};

        return {1, fieldName, json[fieldName]};
    }

    template<>
    Field<nlohmann::json::array_t> Get(const nlohmann::json& json, const std::string& fieldName) {
        if (!json.contains(fieldName))
            return {-1, fieldName, nlohmann::json::array()};

        if (!json[fieldName].is_array())
            return {0, fieldName, nlohmann::json::array()};

        return {1, fieldName, json[fieldName]};
    }

    template<>
    bool GenerateResponse(const Field<int>& field, nlohmann::json& response, Enumerators::RequestCode errorCode) {
        switch(field.opStatus) {
            case -1:
                response = {
                        {"type", errorCode},
                        {"desc", "Field <" + field.name + "> not found"}
                };
                return true;
            case 0:
                response = {
                        {"type", errorCode},
                        {"desc", "Field <" + field.name + "> should be type int"}
                };
                return true;
            default:
                return false;
        }
    }

    template<>
    bool GenerateResponse(const Field<std::string>& field, nlohmann::json& response, Enumerators::RequestCode errorCode) {
        switch(field.opStatus) {
            case -1:
                response = {
                        {"type", errorCode},
                        {"desc", "Field <" + field.name + "> not found"}
                };
                return true;
            case 0:
                response = {
                        {"type", errorCode},
                        {"desc", "Field <" + field.name + "> should be type string"}
                };
                return true;
            default:
                return false;
        }
    }

    template<>
    bool GenerateResponse(const Field<nlohmann::json::array_t>& field, nlohmann::json& response, Enumerators::RequestCode errorCode) {
        switch(field.opStatus) {
            case -1:
                response = {
                        {"type", errorCode},
                        {"desc", "Field <" + field.name + "> not found"}
                };
                return true;
            case 0:
                response = {
                        {"type", errorCode},
                        {"desc", "Field <" + field.name + "> should be type array"}
                };
                return true;
            default:
                return false;
        }
    }

}
