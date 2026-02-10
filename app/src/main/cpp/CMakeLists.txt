cmake_minimum_required(VERSION 3.22.1)
project("sudoku_ndk")

add_library(sudoku_ndk SHARED
    sudoku_engine.cpp
    jni_bridge.cpp
)

find_library(log-lib log)
target_link_libraries(sudoku_ndk ${log-lib})
