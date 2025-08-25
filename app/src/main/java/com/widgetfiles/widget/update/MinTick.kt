package com.widgetfiles.widget.update

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object MinuteTicker {
    const val ACTION_MINUTE_TICK = "com.widgetfiles.widget.ACTION_MINUTE_TICK"
    private fun pi(context: Context) = PendingIntent.getBroadcast(
        context, 0,
        Intent(context, MinuteTickReceiver::class.java).setAction(ACTION_MINUTE_TICK),
        PendingIntent.FLAG_UPDATE_CURRENT or
                (PendingIntent.FLAG_IMMUTABLE)
    )

    fun scheduleNext(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        val now = System.currentTimeMillis()
        val next = now - (now % 60_000L) + 60_000L

        val pending = pi(context)

        //Alarm triggering depending on API version
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    setExactCompat(am, next, pending)
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pending)
                }
            } else {
                setExactCompat(am, next, pending)
            }
        } catch (se: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pending)
        }
    }

    private fun setExactCompat(am: AlarmManager, whenMs: Long, pi: PendingIntent) {
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMs, pi)
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        am.cancel(pi(context))
    }
}
