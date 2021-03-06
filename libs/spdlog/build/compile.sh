#!/bin/bash

ABI=armeabi-v7a
#ABI=x86
#ABI=arm64-v8a
#ABI=x86_64

ANDROID_NDK=$HOME/Android/Sdk/ndk
TOOL_CHAIN=${ANDROID_NDK}/21.1.6352462/build/cmake/android.toolchain.cmake
CMAKE=$HOME/Android/Sdk/cmake/3.10.2.4988404/bin/cmake

mkdir -p ${ABI}
cd ${ABI}

${CMAKE} ../../spdlog -DCMAKE_SYSTEM_NAME=Android -DCMAKE_SYSTEM_VERSION=21 \
-DANDROID_ABI=${ABI} -DCMAKE_TOOLCHAIN_FILE=${TOOL_CHAIN} -DCMAKE_CXX_FLAGS=-DSPDLOG_COMPILED_LIBS

${CMAKE} --build .
