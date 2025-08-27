package com.widgetfiles.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
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
import com.widgetfiles.Native.NativeEngine
import com.widgetfiles.widget.data.PrayerDisplay
import com.widgetfiles.widget.data.PrayerTimes
import com.widgetfiles.widget.location.Prefs
import androidx.glance.Image
import androidx.glance.ImageProvider
import com.widgetfiles.adhanwidget.R
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState

class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val compact = prefs[WidgetKeys.compact] ?: false

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

                if (compact) {
                    AllTimesCompactRow(
                        bgArgb = display.bgColor,
                        fajr = cached.fajr,
                        dhuhr = cached.dhuhr,
                        asr = cached.asr,
                        maghrib = cached.maghrib,
                        isha = cached.isha
                    )
                } else {
                    WidgetUI(display)
                }
            }
        }
    }

    @Composable
    private fun EmptyStateCTA() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    DayNightColorProvider(
                        day = Color.Black,
                        night = Color.Black
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "▶",
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

    fun getPrayerIcon(prayerName: String): Int {
        return when (prayerName.lowercase()) {
            "fajr" -> R.drawable.sunrise
            "dhuhr" -> R.drawable.sun
            "asr" -> R.drawable.timer
            "maghrib" -> R.drawable.sunset
            "isha" -> R.drawable.moon
            else -> R.drawable.sun // fallback
        }
    }

    @Composable
    private fun WidgetUI(display: PrayerDisplay) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(oneColor(display.bgColor))
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Image(
                    provider = ImageProvider(getPrayerIcon(display.prayerName)),
                    contentDescription = "${display.prayerName} icon",
                    modifier = GlanceModifier
                        .size(90.dp)
                        .padding(top = 10.dp, end = 10.dp)
                        .clickable(onClick = actionRunCallback<ToggleCompactAction>())
                )

                Text(
                    text = display.prayerName,
                    style = TextStyle(
                        color = DayNightColorProvider(day = Color.White, night = Color.White),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(end = 12.dp)
                )

                Text(
                    text = display.timeRemaining,
                    style = TextStyle(
                        color = DayNightColorProvider(
                            day = Color.Black,
                            night = Color.Black
                        ),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(end = 12.dp)
                )
            }
        }
    }



    //************************************************************************************************

    //All Prayers UI Function/Toggle
    @Composable
    private fun AllTimesCompactRow(
        bgArgb: Int,
        fajr: Long, dhuhr: Long, asr: Long, maghrib: Long, isha: Long
    ) {
        val items = listOf(
            Triple("Fajr", R.drawable.sunrise, fajr),
            Triple("Dhuhr", R.drawable.sun, dhuhr),
            Triple("Asr", R.drawable.timer, asr),
            Triple("Maghrib", R.drawable.sunset, maghrib),
            Triple("Isha", R.drawable.moon, isha),
        )

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(oneColor(bgArgb))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items.forEach { (name, iconRes, epoch) ->
                    Column(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .padding(horizontal = 4.dp)
                            .clickable(onClick = actionRunCallback<ToggleCompactAction>()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            provider = ImageProvider(iconRes),
                            contentDescription = "$name icon",
                            modifier = GlanceModifier
                                .size(32.dp)
                                .padding(bottom = 4.dp)
                        )
                        Text(
                            text = name,
                            style = TextStyle(
                                color = DayNightColorProvider(day = Color.White, night = Color.White),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = formatLocalTime(epoch),
                            style = TextStyle(
                                color = DayNightColorProvider(
                                    day = Color.Black,
                                    night = Color.Black
                                ),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    private fun formatLocalTime(epochMs: Long): String {
        val df = java.text.SimpleDateFormat("h:mm", java.util.Locale.getDefault())
        df.timeZone = java.util.TimeZone.getDefault()
        return df.format(java.util.Date(epochMs))
    }
}
object WidgetKeys {
    val compact = booleanPreferencesKey("compact")
}

class ToggleCompactAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val now = prefs[WidgetKeys.compact] ?: false
            val mutable = prefs.toMutablePreferences()
            mutable[WidgetKeys.compact] = !now
            mutable
        }
        MyAppWidget().update(context, glanceId)
    }
}