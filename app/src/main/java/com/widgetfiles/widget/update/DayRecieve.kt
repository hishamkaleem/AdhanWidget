package com.widgetfiles.widget.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.widgetfiles.widget.MyAppWidget
import com.widgetfiles.widget.location.Prefs
import com.widgetfiles.widget.location.Prefs.PrayerTimesUtc
import com.widgetfiles.widget.location.LocationRepo
import kotlinx.coroutines.*

class DailyRefreshReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val accepted = setOf(
            DailyRefresher.ACTION,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_DATE_CHANGED
        )
        if (intent?.action !in accepted) return

        val pending = goAsync()
        val appCtx = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val mustRecalc =
                    Prefs.isNewDay(appCtx) ||
                            Prefs.readTimes(appCtx) == null ||
                            intent?.action != DailyRefresher.ACTION

                if (mustRecalc) {
                    val loc = LocationRepo.getLatLng(appCtx)
                        ?: Prefs.readLocation(appCtx)
                        ?: (43.6532 to -79.3832) // Toronto fallback

                    val zone = java.time.ZoneId.systemDefault()
                    val today = java.time.ZonedDateTime.now(zone)

                    runCatching {
                        val arr = com.widgetfiles.Native.NativeEngine.computeUTC(
                            today.year, today.monthValue, today.dayOfMonth,
                            loc.first, loc.second,
                            15.0, 15.0, 0.833, 1.0
                        )
                        Prefs.saveLocation(appCtx, loc.first, loc.second)
                        Prefs.saveTimes(
                            appCtx,
                            PrayerTimesUtc(arr.fajr, arr.sunrise,arr.dhuhr, arr.asr, arr.maghrib, arr.isha),
                            blocking = true
                        )
                        Prefs.markToday(appCtx, blocking = true)
                    }
                }

                val mgr = GlanceAppWidgetManager(appCtx)
                val ids = mgr.getGlanceIds(MyAppWidget::class.java)
                val widget = MyAppWidget()
                ids.forEach { widget.update(appCtx, it) }

            } finally {
                DailyRefresher.scheduleNext(appCtx)
                pending.finish()
            }
        }
    }
}
