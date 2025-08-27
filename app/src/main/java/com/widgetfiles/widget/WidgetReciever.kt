package com.widgetfiles.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.widgetfiles.widget.update.DailyRefresher
import com.widgetfiles.widget.update.MinuteTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        MinuteTicker.scheduleNext(context)
        DailyRefresher.scheduleNext(context, 0, 5)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val appCtx = context.applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            val glanceManager = GlanceAppWidgetManager(appCtx)
            val glanceIds: List<GlanceId> =
                glanceManager.getGlanceIds(MyAppWidget::class.java)

            val widget = MyAppWidget()
            glanceIds.forEach { id ->
                widget.update(appCtx, id)
            }
        }
        MinuteTicker.scheduleNext(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        MinuteTicker.cancel(context)
        DailyRefresher.cancel(context)
    }
}
