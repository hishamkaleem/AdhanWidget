package com.widgetfiles.widget.location

import android.content.Context
import androidx.core.content.edit

object Prefs {
    private const val N = "adhan_prefs"
    private const val LAT = "lat"; private const val LNG = "lng"; private const val YMD = "ymd"

    private fun sp(ctx: Context) =
        ctx.applicationContext.getSharedPreferences(N, Context.MODE_PRIVATE)

    fun saveLocation(ctx: Context, lat: Double, lng: Double, blocking: Boolean = false) =
        sp(ctx).edit(commit = blocking) {
            putString(LAT, lat.toString())
            putString(LNG, lng.toString())
        }

    fun readLocation(ctx: Context): Pair<Double, Double>? = sp(ctx).let { sp ->
        val la = sp.getString(LAT, null)?.toDoubleOrNull()
        val ln = sp.getString(LNG, null)?.toDoubleOrNull()
        if (la != null && ln != null) la to ln else null
    }

    fun markToday(ctx: Context, blocking: Boolean = false) =
        sp(ctx).edit(commit = blocking) {
            putString(YMD, java.time.LocalDate.now().toString())
        }

    fun isNewDay(ctx: Context): Boolean =
        sp(ctx).getString(YMD, null) != java.time.LocalDate.now().toString()

    data class PrayerTimesUtc(
        val fajr: Long, val dhuhr: Long, val asr: Long, val maghrib: Long, val isha: Long
    )

    private const val FJ = "pt_fajr"; private const val DH = "pt_dhuhr"
    private const val AS = "pt_asr";  private const val MG = "pt_maghrib"; private const val IS = "pt_isha"

    fun saveTimes(ctx: Context, t: PrayerTimesUtc, blocking: Boolean = false) =
        sp(ctx).edit(commit = blocking) {
            putLong(FJ, t.fajr)
            putLong(DH, t.dhuhr)
            putLong(AS, t.asr)
            putLong(MG, t.maghrib)
            putLong(IS, t.isha)
        }

    fun readTimes(ctx: Context): PrayerTimesUtc? = sp(ctx).let { sp ->
        if (!sp.contains(FJ)) return null
        PrayerTimesUtc(
            sp.getLong(FJ, 0L),
            sp.getLong(DH, 0L),
            sp.getLong(AS, 0L),
            sp.getLong(MG, 0L),
            sp.getLong(IS, 0L)
        )
    }
}
