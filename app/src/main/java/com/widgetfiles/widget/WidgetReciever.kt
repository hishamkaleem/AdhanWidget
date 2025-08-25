package com.widgetfiles.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.widgetfiles.widget.update.MinuteTicker
import com.widgetfiles.widget.update.DailyRefresher

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        MinuteTicker.cancel(context)
        MinuteTicker.scheduleNext(context)
        DailyRefresher.scheduleNext(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        MinuteTicker.cancel(context)
    }
}
