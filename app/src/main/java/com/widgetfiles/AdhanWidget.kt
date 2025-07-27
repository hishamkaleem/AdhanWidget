package com.widgetfiles

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.color.ColorProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import java.util.Calendar

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetUI()
        }
    }

    data class Prayer(val name: String, val time: String)

    // Mocked prayer times provider
    private fun getPrayerTimes(): List<Prayer> = listOf(
        Prayer("Fajr", "05:00"),
        Prayer("Dhuhr", "12:30"),
        Prayer("Asr", "15:45"),
        Prayer("Maghrib", "18:20"),
        Prayer("Isha", "20:00")
    )

    // Dynamic color provider based on time
    private fun getDynamicColor(): ColorProvider {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> ColorProvider(Color(0xFF81D4FA)) // Morning: light blue
            in 12..16 -> ColorProvider(Color(0xFFFFF176)) // Afternoon: yellow
            in 17..19 -> ColorProvider(Color(0xFFFF8A65)) // Evening: orange
            else -> ColorProvider(Color(0xFF424242)) // Night: dark gray
        }
    }

    @Composable
    private fun WidgetUI() {
        val icons = listOf(
            "\uD83C\uDF05",
            "\uD83C\uDF24\uFE0F",
            "\u2600\uFE0F",
            "\uD83C\uDF07",
            "\uD83C\uDF19"
        ) // sunrise, sun behind cloud, sun, sunset, moon
        val labels = listOf("First", "Second", "Third", "Fourth", "Fifth")
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(getDynamicColor())
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in icons.indices) {
                    Column(
                        modifier = GlanceModifier.defaultWeight().padding(horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = icons[i],
                            style = TextStyle(fontSize = 32.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = labels[i],
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                    }
                    if (i != icons.lastIndex) {
                        Spacer(
                            modifier = GlanceModifier.width(2.dp).fillMaxHeight()
                                .background(ColorProvider(Color.White))
                        )
                    }
                }
            }
        }
    }
}
