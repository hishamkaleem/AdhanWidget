package com.widgetfiles.widget.data

data class PrayerTimes(
    val fajr: Long,
    val sunrise: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long
)
data class PrayerDisplay(
    val prayerName: String,
    val timeRemaining: String,
    val bgColor: Int
)