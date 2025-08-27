package com.widgetfiles.widget.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.widgetfiles.widget.MyAppWidget
import kotlinx.coroutines.*

class MinuteTickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != MinuteTicker.ACTION_MINUTE_TICK) return

        val pending = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                val mgr = GlanceAppWidgetManager(appContext)
                val ids = mgr.getGlanceIds(MyAppWidget::class.java)
                val widget = MyAppWidget()

                ids.forEach { id ->
                    widget.update(appContext, id)
                }
            } finally {
                MinuteTicker.scheduleNext(appContext)
                pending.finish()
            }
        }
    }
}
