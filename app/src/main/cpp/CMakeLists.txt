cmake_minimum_required(VERSION 3.22.1)
project(cppscript)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(cppscript SHARED
        main.cpp
        prayertimes/prayertimes.cpp
)

target_include_directories(cppscript PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/prayertimes
)

find_library(log-lib log)
target_link_libraries(cppscript ${log-lib})
