package com.widgetfiles.widget.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.*
import com.widgetfiles.widget.MyAppWidget

class MinuteTickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != MinuteTicker.ACTION_MINUTE_TICK) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val mgr = GlanceAppWidgetManager(context)
                val ids = mgr.getGlanceIds(MyAppWidget::class.java)
                val widget = MyAppWidget()
                ids.forEach { id -> widget.update(context, id) }
            } finally {
                pending.finish()
            }
        }
        MinuteTicker.scheduleNext(context)
    }
}
