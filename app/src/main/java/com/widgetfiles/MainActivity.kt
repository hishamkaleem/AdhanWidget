package com.widgetfiles

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.widgetfiles.Native.NativeEngine
import com.widgetfiles.widget.MyAppWidget
import com.widgetfiles.widget.location.Prefs
import com.widgetfiles.widget.update.DailyRefresher
import com.widgetfiles.widget.update.MinuteTicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val requestPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        lifecycleScope.launch(Dispatchers.IO) { seedTodayAndRefresh(forceFresh = granted) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val am = getSystemService(AlarmManager::class.java)
        if (!am.canScheduleExactAlarms()) {
            try {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            } catch (_: Exception) {
            }
        }

        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fine   = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (coarse == PackageManager.PERMISSION_GRANTED || fine == PackageManager.PERMISSION_GRANTED) {
            lifecycleScope.launch(Dispatchers.IO) { seedTodayAndRefresh(forceFresh = true) }
        } else {
            requestPerms.launch(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
    }

    private suspend fun seedTodayAndRefresh(forceFresh: Boolean) {
        val appCtx = applicationContext

        val lat = 43.6532 //Default to Toronto
        val lng = -79.3832

        val now = java.time.ZonedDateTime.now(java.time.ZoneId.systemDefault())
        val arr = NativeEngine.computeUTC(
            now.year, now.monthValue, now.dayOfMonth,
            lat, lng,
            15.0, 15.0, 0.833, 1.0
        )

        Prefs.saveLocation(appCtx, lat, lng)
        Prefs.saveTimes(appCtx, Prefs.PrayerTimesUtc(arr.fajr, arr.dhuhr, arr.asr, arr.maghrib, arr.isha))
        Prefs.markToday(appCtx)

        val mgr = GlanceAppWidgetManager(appCtx)
        val ids = mgr.getGlanceIds(MyAppWidget::class.java)
        val widget = MyAppWidget()
        ids.forEach { widget.update(appCtx, it) }

        MinuteTicker.nudgeNowAndScheduleNext(appCtx)
        DailyRefresher.scheduleNext(appCtx, hour = 0, minute = 5)

        withContext(Dispatchers.Main) { finish() }
    }
}
