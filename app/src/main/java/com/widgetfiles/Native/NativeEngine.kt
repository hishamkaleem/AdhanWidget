package com.widgetfiles.Native

object NativeEngine {
    init { System.loadLibrary("cppscript") }

    @JvmStatic external fun computeISNA(
        year: Int, month: Int, day: Int,
        lat: Double, lng: Double,
        utcOffsetMinutes: Int
    ): LongArray
}
