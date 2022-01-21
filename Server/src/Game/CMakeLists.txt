include_directories(../ ../../lib/json/include)

add_subdirectory(Participants)

add_library(Game
        Game.cpp
        )

target_link_directories(Game
        PRIVATE Game.Participants
        )

target_link_libraries(Game
        Game.Participants
        )
