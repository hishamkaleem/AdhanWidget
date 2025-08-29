package com.widgetfiles.widget.vibrate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.widgetfiles.widget.location.Prefs

class PrayerVibrateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext
        if (!Prefs.isVibrateOn(app)) return
        Vibrations.strongFiveSecondsPattern(context.applicationContext)
    }
}
