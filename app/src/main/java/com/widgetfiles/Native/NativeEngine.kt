package com.widgetfiles.Native

object NativeEngine {
    init {
        System.loadLibrary("adhanengine")
    }

    external fun WidgetMessage(): String
}