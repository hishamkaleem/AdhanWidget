package com.widgetfiles.widget.update

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.ZoneId
import java.time.ZonedDateTime

object DailyRefresher {
    const val ACTION = "com.widgetfiles.widget.ACTION_DAILY_REFRESH"
    @android.annotation.SuppressLint("ScheduleExactAlarm")
    fun scheduleNext(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        val zone = java.time.ZoneId.systemDefault()
        val next = java.time.ZonedDateTime.now(zone).toLocalDate()
            .plusDays(1).atTime(0,5).atZone(zone).toInstant().toEpochMilli()
        val pi = PendingIntent.getBroadcast(ctx,2002, Intent(ctx, DailyRefreshReceiver::class.java).setAction(ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT or (PendingIntent.FLAG_IMMUTABLE))
        try {
            if (Build.VERSION.SDK_INT>=31 && !am.canScheduleExactAlarms())
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next,pi)
            else am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next,pi)
        } catch (_: SecurityException) { am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next,pi) }
    }
}
