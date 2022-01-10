cmake_minimum_required(VERSION 3.10)

project(PP-5-SK2-quiz)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_FLAGS_INIT "-Werror -Wall -Wextra -Wpedantic")

set(HEADERS src/Server.h src/util.h)
set(SOURCES src/Server.cpp src/util.cpp)

add_library(SRC SHARED ${HEADERS} ${SOURCES})

add_executable(server_app src/main.cpp)
target_link_libraries(server_app SRC)
target_precompile_headers(server_app PRIVATE src/pch.h)