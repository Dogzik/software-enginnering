cmake_minimum_required(VERSION 3.10)
project(lru-cache)

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/lib)

set(Boost_USE_STATIC_LIBS ON)
set(Boost_USE_MULTITHREADED ON)
set(Boost_USE_STATIC_RUNTIME ON)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

find_package(Boost)
if(Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIRS})
endif()

include(FindGTest)
add_executable(test test/test.cpp)
target_link_libraries(test GTest::GTest)
