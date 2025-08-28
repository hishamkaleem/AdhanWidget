package com.widgetfiles.widget.update

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import java.util.Calendar

object MinuteTicker {
    const val ACTION_MINUTE_TICK = "com.widgetfiles.widget.ACTION_MINUTE_TICK"
    private const val REQ_CODE = 1001

    private fun intent(ctx: Context) =
        Intent(ctx, MinuteTickReceiver::class.java).setAction(ACTION_MINUTE_TICK)

    fun scheduleNext(context: Context): Long {
        val am = context.getSystemService(AlarmManager::class.java)
        val now = System.currentTimeMillis()
        val next = (now / 60_000L + 1) * 60_000L
        val pi = PendingIntent.getBroadcast(
            context, REQ_CODE, intent(context),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        AlarmManagerCompat.setExactAndAllowWhileIdle(am, AlarmManager.RTC_WAKEUP, next, pi)
        return next
    }

    fun nudgeNowAndScheduleNext(context: Context) {
        context.sendBroadcast(intent(context))
        scheduleNext(context)
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        val pi = PendingIntent.getBroadcast(
            context, REQ_CODE, intent(context),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) am.cancel(pi)
    }
}

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

