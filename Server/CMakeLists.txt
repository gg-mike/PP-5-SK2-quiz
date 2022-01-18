cmake_minimum_required(VERSION 3.16)
project(Server)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_FLAGS_INIT "-Werror -Wall -Wextra -Wpedantic")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread -std=c++2a")

# OPTIONS
option(DEBUG "Enable tools for debug (like logging)" ON)

if (DEBUG)
    add_definitions(-D_DEBUG)
endif()

# SUBDIRECTORIES
add_subdirectory(src)
add_subdirectory(lib/json)
