package com.widgetfiles

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.widgetfiles.Native.NativeEngine
import com.widgetfiles.widget.MyAppWidget
import com.widgetfiles.widget.location.Prefs
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        lifecycleScope.launch { seedTodayAndRefresh(forceFresh = granted) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fine   = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (coarse == PackageManager.PERMISSION_GRANTED || fine == PackageManager.PERMISSION_GRANTED) {
            lifecycleScope.launch { seedTodayAndRefresh(forceFresh = true) }
        } else {
            requestPerms.launch(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
    }

    private suspend fun seedTodayAndRefresh(forceFresh: Boolean) {
        //val loc = if (forceFresh) LocationRepo.getLatLng(this) else null
        //val (lat, lng) = loc ?: Prefs.readLocation(this) ?: (43.6532 to -79.3832)
        val lat = 43.6532 //Toronto coords for testing
        val lng = -79.3832
        val now = java.time.ZonedDateTime.now(java.time.ZoneId.systemDefault())
        val arr = NativeEngine.computeUTC(
            now.year, now.monthValue, now.dayOfMonth,
            lat, lng,
            15.0, 15.0, 0.833, 1.0
        )

        Prefs.saveLocation(this, lat, lng)
        Prefs.saveTimes(this, Prefs.PrayerTimesUtc(arr.fajr, arr.dhuhr, arr.asr, arr.maghrib, arr.isha))
        Prefs.markToday(this)

        val mgr = GlanceAppWidgetManager(this)
        val ids = mgr.getGlanceIds(MyAppWidget::class.java)
        val widget = MyAppWidget()
        ids.forEach { widget.update(this, it) }

        finish()
    }
}
