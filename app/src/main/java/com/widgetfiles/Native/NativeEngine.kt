package com.widgetfiles.Native

import com.widgetfiles.widget.data.PrayerDisplay

object NativeEngine {
    init { System.loadLibrary("cppscript") }
    external fun widgetInfoDisplay(
        utcOffsetMinutes: Int
    ): PrayerDisplay
}
