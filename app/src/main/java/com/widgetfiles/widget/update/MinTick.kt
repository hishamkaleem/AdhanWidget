package com.widgetfiles.widget.update

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object MinuteTicker {
    const val ACTION_MINUTE_TICK = "com.widgetfiles.widget.ACTION_MINUTE_TICK"
    private const val REQ_CODE = 1001

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MinuteTickReceiver::class.java)
            .setAction(ACTION_MINUTE_TICK)
        return PendingIntent.getBroadcast(
            context,
            REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun scheduleNext(context: Context): Long {
        val am = context.getSystemService(AlarmManager::class.java)
        val pi = pendingIntent(context)

        am.cancel(pi)

        val now = System.currentTimeMillis()
        val next = ((now / 60_000L) + 1) * 60_000L

        try {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pi)
        } catch (_: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pi)
        }
        return next
    }

    fun nudgeNowAndScheduleNext(context: Context) {
        context.sendBroadcast(
            Intent(context, MinuteTickReceiver::class.java).setAction(ACTION_MINUTE_TICK)
        )
        scheduleNext(context)
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        am.cancel(pendingIntent(context))
    }
}
