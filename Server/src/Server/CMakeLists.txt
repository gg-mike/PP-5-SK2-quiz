include_directories(../ ../../lib/json/include)

add_library(Server
        Server.cpp
        Client.cpp
        Database.cpp
        )

target_link_libraries(Server Game)