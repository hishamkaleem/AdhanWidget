package com.widgetfiles.widget.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.widgetfiles.Native.NativeEngine
import com.widgetfiles.widget.MyAppWidget
import com.widgetfiles.widget.location.Prefs
import com.widgetfiles.widget.location.Prefs.PrayerTimesUtc
import com.widgetfiles.widget.location.LocationRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
class DailyRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != DailyRefresher.ACTION) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val need = Prefs.isNewDay(context) || Prefs.readTimes(context) == null
                if (need) {
                    val loc = LocationRepo.getLatLng(context)
                        ?: Prefs.readLocation(context)
                        ?: (43.6532 to -79.3832) // fallback
                    val zone = java.time.ZoneId.systemDefault()
                    val now  = java.time.ZonedDateTime.now(zone)
                    val arr  = com.widgetfiles.Native.NativeEngine.computeUTC(
                        now.year, now.monthValue, now.dayOfMonth,
                        loc.first, loc.second,
                        15.0, 15.0, 0.833, 1.0
                    )
                    Prefs.saveLocation(context, loc.first, loc.second)
                    Prefs.saveTimes(context, PrayerTimesUtc(arr.fajr,arr.dhuhr,arr.asr,arr.maghrib,arr.isha))
                    Prefs.markToday(context)
                }

                val mgr = GlanceAppWidgetManager(context)
                val ids = mgr.getGlanceIds(MyAppWidget::class.java)
                val widget = MyAppWidget()
                ids.forEach { widget.update(context, it) }
            } finally {
                DailyRefresher.scheduleNext(context)
                pending.finish()
            }
        }
    }
}