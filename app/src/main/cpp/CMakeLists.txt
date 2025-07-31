cmake_minimum_required(VERSION 3.10.2)
project("adhanengine")

add_library(
        adhanengine
        SHARED
        adhanengine.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        adhanengine
        ${log-lib}
)