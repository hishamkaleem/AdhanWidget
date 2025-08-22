package com.widgetfiles.Native

import com.widgetfiles.widget.PrayerDisplay

object NativeEngine {
    init { System.loadLibrary("cppscript") }

    external fun widgetInfoDisplay(
        year: Int, month: Int, day: Int,
        lat: Double, lng: Double,
        utcOffsetMinutes: Int
    ): PrayerDisplay
}
