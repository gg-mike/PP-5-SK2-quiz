cmake_minimum_required(VERSION 3.10)

project(Server)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_FLAGS_INIT "-Werror -Wall -Wextra -Wpedantic")

# OPTIONS
option(DEBUG "Enable tools for debug (like logging)" ON)

if (DEBUG)
    add_definitions(-D_DEBUG)
endif()

# SUBDIRECTORIES
add_subdirectory(src)
add_subdirectory(lib/json)