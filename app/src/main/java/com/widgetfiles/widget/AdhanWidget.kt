package com.widgetfiles.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import androidx.glance.color.ColorProvider as DayNightColorProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetfiles.Native.NativeEngine.computeISNA
import java.util.Calendar


class MyAppWidget : GlanceAppWidget() {
    data class Prayer(val name: String, val time: String, val icon: String)

    private fun getPrayerTimesSmart(context: Context): List<Prayer> {
        return listOf(
            Prayer("Fajr", "05:00", "🌄"),
            Prayer("Dhuhr", "12:30", "☀️"),
            Prayer("Asr", "15:45", "\uD83D\uDD57"),
            Prayer("Maghrib", "18:20", "🌇"),
            Prayer("Isha", "20:00", "🌙")
        )
    }

    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        provideContent {
            //val cppMessage = WidgetMessage()
            WidgetUI(getPrayerTimesSmart(context), "test")
        }
    }

    @Composable
    private fun WidgetUI(prayers: List<Prayer>, cppMessage: String) {
        val (current, next) = getCurrentAndNextPrayer(prayers)
        val display = next

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(getDynamicColor())
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cppMessage,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = DayNightColorProvider(day = Color.Yellow, night = Color.Yellow)
                ),
                modifier = GlanceModifier.padding(bottom = 8.dp)
            )

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
                    text = display.name,
                    style = TextStyle(
                        color = DayNightColorProvider(day = Color.White, night = Color.White),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(end = 18.dp)
                )
                Text(
                    text = display.time,
                    style = TextStyle(
                        color = DayNightColorProvider(
                            day = Color(0xFFB3C6FF),
                            night = Color(0xFFB3C6FF)
                        ),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    private fun getCurrentAndNextPrayer(prayers: List<Prayer>): Pair<Prayer, Prayer> {
        val now = Calendar.getInstance()
        val nowMins = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        var current = prayers.first()
        var next = prayers.first()
        for (i in prayers.indices) {
            val mins = prayers[i].time.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            if (nowMins >= mins) current = prayers[i]
            if (nowMins < mins) {
                next = prayers[i]; break
            }
        }
        if (nowMins >= prayers.last().time.split(":")
                .let { it[0].toInt() * 60 + it[1].toInt() }
        ) next = prayers.first()
        return current to next
    }

    private fun getDynamicColor(): ColorProvider {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> DayNightColorProvider(day = Color(0xFF81D4FA), night = Color(0xFF81D4FA))
            in 12..16 -> DayNightColorProvider(day = Color(0xFFFFF176), night = Color(0xFFFFF176))
            in 17..19 -> DayNightColorProvider(day = Color(0xFFFF8A65), night = Color(0xFFFF8A65))
            else -> DayNightColorProvider(day = Color(0xFF21242A), night = Color(0xFF21242A))
        }
    }
}
