package com.widgetfiles.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.color.ColorProvider as DayNightColorProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetfiles.Native.NativeEngine
import com.widgetfiles.widget.data.PrayerDisplay
import com.widgetfiles.widget.data.PrayerTimes
import com.widgetfiles.widget.location.Prefs
import com.widgetfiles.MainActivity

class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        provideContent {
            val cached = Prefs.readTimes(context)

            if (cached == null) {
                EmptyStateCTA()
            } else {
                val pt = PrayerTimes(
                    fajr = cached.fajr,
                    dhuhr = cached.dhuhr,
                    asr = cached.asr,
                    maghrib = cached.maghrib,
                    isha = cached.isha
                )
                val display: PrayerDisplay = NativeEngine.widgetInfoDisplay(pt)
                WidgetUI(display)
            }
        }
    }

    @Composable
    private fun EmptyStateCTA() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(DayNightColorProvider(day = Color(0xFF1B1E23), night = Color(0xFF1B1E23)))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📍",
                style = TextStyle(
                    fontSize = 40.sp,
                    color = DayNightColorProvider(day = Color.White, night = Color.White)
                ),
                modifier = GlanceModifier
                    .clickable(onClick = actionStartActivity<com.widgetfiles.MainActivity>())
            )
        }
    }

    private fun oneColor(argb: Int): ColorProvider {
        val c = Color(argb)
        return DayNightColorProvider(day = c, night = c)
    }

    @Composable
    private fun WidgetUI(display: PrayerDisplay) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(oneColor(display.bgColor))
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = display.icon,
                    style = TextStyle(
                        fontSize = 42.sp,
                        color = DayNightColorProvider(day = Color.White, night = Color.White)
                    ),
                    modifier = GlanceModifier.padding(end = 16.dp)
                )
                Text(
                    text = display.prayerName,
                    style = TextStyle(
                        color = DayNightColorProvider(day = Color.White, night = Color.White),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(end = 18.dp)
                )
                Text(
                    text = display.timeRemaining,
                    style = TextStyle(
                        color = DayNightColorProvider(day = Color(0xFFB3C6FF), night = Color(0xFFB3C6FF)),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
