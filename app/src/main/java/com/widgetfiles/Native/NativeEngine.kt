package com.widgetfiles.Native

object NativeEngine {
    init {
        System.loadLibrary("adhanengine") // Must match CMake library name
    }
}