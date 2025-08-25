package com.widgetfiles.widget.location

import android.content.Context
import java.time.LocalDate
import androidx.core.content.edit

object Prefs {
    private const val N = "adhan_prefs"
    private const val LAT = "lat"; private const val LNG = "lng"; private const val YMD = "ymd"

    fun saveLocation(ctx: Context, lat: Double, lng: Double) =
        ctx.getSharedPreferences(N, Context.MODE_PRIVATE).edit {
            putString(LAT, lat.toString()).putString(LNG, lng.toString())
        }

    fun readLocation(ctx: Context): Pair<Double, Double>? = ctx
        .getSharedPreferences(N, Context.MODE_PRIVATE).let { sp ->
            val la = sp.getString(LAT, null)?.toDoubleOrNull()
            val ln = sp.getString(LNG, null)?.toDoubleOrNull()
            if (la != null && ln != null) la to ln else null
        }

    fun markToday(ctx: Context) = ctx.getSharedPreferences(N, Context.MODE_PRIVATE).edit {
        putString(YMD, java.time.LocalDate.now().toString())
    }

    fun isNewDay(ctx: Context): Boolean {
        val sp = ctx.getSharedPreferences(N, Context.MODE_PRIVATE)
        return sp.getString(YMD, null) != java.time.LocalDate.now().toString()
    }

    data class PrayerTimesUtc(
        val fajr: Long, val dhuhr: Long, val asr: Long, val maghrib: Long, val isha: Long
    )

    private const val FJ = "pt_fajr"; private const val DH = "pt_dhuhr"
    private const val AS = "pt_asr";  private const val MG = "pt_maghrib"; private const val IS = "pt_isha"

    fun saveTimes(ctx: Context, t: PrayerTimesUtc) =
        ctx.getSharedPreferences(N, Context.MODE_PRIVATE).edit {
            putLong(FJ, t.fajr); putLong(DH, t.dhuhr); putLong(AS, t.asr)
            putLong(MG, t.maghrib); putLong(IS, t.isha)
        }

    fun readTimes(ctx: Context): PrayerTimesUtc? {
        val sp = ctx.getSharedPreferences(N, Context.MODE_PRIVATE)
        if (!sp.contains(FJ)) return null
        return PrayerTimesUtc(
            sp.getLong(FJ, 0L), sp.getLong(DH, 0L),
            sp.getLong(AS, 0L), sp.getLong(MG, 0L), sp.getLong(IS, 0L)
        )
    }
}
