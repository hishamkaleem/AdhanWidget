package com.widgetfiles.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.widgetfiles.widget.update.DailyRefresher
import com.widgetfiles.widget.update.MinuteTicker

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        MinuteTicker.scheduleNext(context)
        DailyRefresher.scheduleNext(context)

        context.sendBroadcast(
            Intent(DailyRefresher.ACTION).setPackage(context.packageName)
        )
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        MinuteTicker.cancel(context)
    }
}
