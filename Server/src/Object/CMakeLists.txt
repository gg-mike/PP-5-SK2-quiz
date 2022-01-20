include_directories(../ ../../lib/json/include)

add_library(Object
        Server.cpp
        Client.cpp
        )

target_link_libraries(Object Game.Participants)