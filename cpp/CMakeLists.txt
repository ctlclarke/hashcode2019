cmake_minimum_required(VERSION 3.0)
project(hash19)
file(GLOB_RECURSE sources src/*.cpp src/*.h)
add_executable(hash19 ${sources})

include(CheckCXXCompilerFlag)
CHECK_CXX_COMPILER_FLAG("-march=native" COMPILER_SUPPORTS_MARCH_NATIVE)
if(COMPILER_SUPPORTS_MARCH_NATIVE)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=native")
endif()

target_compile_options(hash19 PUBLIC -std=c++17 -Wall -O3)
#target_compile_options(hash19 PUBLIC -std=c++17 -Wall -pg)
target_link_libraries(hash19 PUBLIC pthread)
# Use the following for profiling:
#target_link_libraries(hash19 PUBLIC -pg)
target_include_directories(hash19 PUBLIC src)
