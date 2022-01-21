include_directories(../ ../../lib/json/include)

add_library(Object
        Server.cpp
        Client.cpp
        Database.cpp
        )

target_link_libraries(Object Game)