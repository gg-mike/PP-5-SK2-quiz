add_subdirectory(Game)
add_subdirectory(Object)
add_subdirectory(Utility)

add_executable(${PROJECT_NAME} main.cpp)

target_include_directories(${PROJECT_NAME}
        PUBLIC ../lib/json/include
        )

target_link_directories(${PROJECT_NAME}
        PRIVATE Game
        PRIVATE Object
        PRIVATE Utility
        )

target_link_libraries(${PROJECT_NAME}
        Game
        Object
        Utility
        )

target_precompile_headers(${PROJECT_NAME} PRIVATE pch.h)

add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_directory
        "${PROJECT_SOURCE_DIR}/res"
        $<TARGET_FILE_DIR:${PROJECT_NAME}>/res
)
