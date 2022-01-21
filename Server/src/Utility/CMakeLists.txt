include_directories(../ ../../lib/json/include)

add_library(Utility
        Log.cpp
        IO.cpp
        Socket.cpp
        Json.cpp
        )
