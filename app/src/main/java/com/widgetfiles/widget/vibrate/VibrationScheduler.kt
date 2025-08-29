package com.widgetfiles.widget.vibrate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.widgetfiles.widget.location.Prefs

object PrayerAlarmScheduler {
    fun scheduleSixFromPrefs(context: Context) {
        val t = Prefs.readTimes(context) ?: return
        val now = System.currentTimeMillis()

        val prayers = listOf(
            "FAJR"    to t.fajr,
            "SUNRISE" to t.sunrise,
            "DHUHR"   to t.dhuhr,
            "ASR"     to t.asr,
            "MAGHRIB" to t.maghrib,
            "ISHA"    to t.isha
        )

        cancelAll(context)

        prayers.forEach { (name, whenMs) ->
            if (whenMs > now) setExact(context, requestCode(name), whenMs, name)
        }
    }

    fun rescheduleFromPrefs(context: Context) {
        cancelAll(context)
        scheduleSixFromPrefs(context)
    }

    fun cancelAll(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        arrayOf("FAJR","SUNRISE","DHUHR","ASR","MAGHRIB","ISHA").forEach { name ->
            val pi = buildPI(context, name, PendingIntent.FLAG_NO_CREATE) ?: return@forEach
            am.cancel(pi)
        }
    }

    private fun minuteStart(ms: Long): Long = (ms / 60_000L) * 60_000L
    private fun setExact(context: Context, reqCode: Int, triggerAtMillisRaw: Long, prayerName: String) {
        val am = context.getSystemService(AlarmManager::class.java)
        val triggerAtMillis = minuteStart(triggerAtMillisRaw)

        val intent = Intent(context, PrayerVibrateReceiver::class.java)
            .setAction("com.widgetfiles.widget.PRAYER_ALARM")
            .putExtra("prayer", prayerName)

        val pi = PendingIntent.getBroadcast(
            context, reqCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
            !am.canScheduleExactAlarms()
        ) {
            am.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAtMillis, pi), pi)
            return
        }

        androidx.core.app.AlarmManagerCompat.setExactAndAllowWhileIdle(
            am, AlarmManager.RTC_WAKEUP, triggerAtMillis, pi
        )
    }

    private fun buildPI(context: Context, prayerName: String, flag: Int): PendingIntent? {
        val intent = Intent("com.widgetfiles.widget.PRAYER_ALARM")
            .setClass(context, PrayerVibrateReceiver::class.java)
            .putExtra("prayer", prayerName)

        return PendingIntent.getBroadcast(
            context,
            requestCode(prayerName),
            intent,
            flag or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun requestCode(prayerName: String): Int = when (prayerName) {
        "FAJR" -> 1001
        "SUNRISE" -> 1002
        "DHUHR" -> 1003
        "ASR" -> 1004
        "MAGHRIB" -> 1005
        "ISHA" -> 1006
        else -> 1999
    }
}
