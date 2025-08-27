package com.widgetfiles.widget.update

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object DailyRefresher {
    const val ACTION = "com.widgetfiles.widget.ACTION_DAILY_REFRESH"
    private const val REQ_CODE = 2002

    private fun pendingIntent(ctx: Context): PendingIntent {
        val intent = Intent(ctx, DailyRefreshReceiver::class.java)
            .setAction(ACTION)
            .setPackage(ctx.packageName)
        return PendingIntent.getBroadcast(
            ctx,
            REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun scheduleNext(ctx: Context, hour: Int = 0, minute: Int = 5) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        val pi = pendingIntent(ctx)

        am.cancel(pi)

        val cal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val whenMs = cal.timeInMillis

        try {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMs, pi)
        } catch (_: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMs, pi)
        }
    }

    fun runNowThenScheduleNext(ctx: Context, hour: Int = 0, minute: Int = 5) {
        ctx.sendBroadcast(
            Intent(ctx, DailyRefreshReceiver::class.java)
                .setAction(ACTION)
                .setPackage(ctx.packageName)
        )
        scheduleNext(ctx, hour, minute)
    }

    fun cancel(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        am.cancel(pendingIntent(ctx))
    }
}
