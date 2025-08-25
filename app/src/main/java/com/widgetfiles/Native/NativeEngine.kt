package com.widgetfiles.Native

import com.widgetfiles.widget.data.PrayerDisplay
import com.widgetfiles.widget.data.PrayerTimes

object NativeEngine {
    init { System.loadLibrary("cppscript") }
    external fun computeUTC(
        year:Int, month:Int, day:Int,
        lat:Double, lng:Double,
        fajrAngle:Double, ishaAngle:Double, horizonDeg:Double, asrShadow:Double
    ): PrayerTimes
    external fun widgetInfoDisplay(pt: PrayerTimes): PrayerDisplay
}
