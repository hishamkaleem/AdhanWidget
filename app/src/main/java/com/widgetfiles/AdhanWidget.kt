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
import androidx.compose.ui.unit.TextUnit
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetUI()
        }
    }

    @Composable
    private fun WidgetUI() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(androidx.glance.unit.ColorProvider(Color(0xFFFF0000))) // Red background
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello world!",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFFFFFF),Color(0xFFFFFFFF)),
                        fontFamily = FontFamily("Cursive"),
                    )
                )
            }
        }
    }
}
