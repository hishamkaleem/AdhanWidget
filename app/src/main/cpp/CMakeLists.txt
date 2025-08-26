cmake_minimum_required(VERSION 3.22.1)
project(cppscript LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR})

add_library(cppscript SHARED
        ${SRC_DIR}/main.cpp
        ${SRC_DIR}/prayer/prayercalc/prayercalc.cpp
        ${SRC_DIR}/prayer/prayertimes.cpp
)

target_include_directories(cppscript PRIVATE
        ${SRC_DIR}/prayer
        ${SRC_DIR}/prayer/prayercalc
)

if(ANDROID)
    # Android-only stuff
    target_link_libraries(cppscript PRIVATE log)
    target_link_options(cppscript PRIVATE "-Wl,-z,max-page-size=16384")
endif()
