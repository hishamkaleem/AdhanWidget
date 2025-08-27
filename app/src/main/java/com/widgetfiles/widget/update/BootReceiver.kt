package com.widgetfiles.widget.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        MinuteTicker.scheduleNext(context)
        DailyRefresher.scheduleNext(context, 0, 5)
    }
}
