package com.widgetfiles.widget.vibrate

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.widgetfiles.widget.MyAppWidget
import com.widgetfiles.widget.WidgetKeys
import com.widgetfiles.widget.location.Prefs

class ToggleVibrateAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val app = context.applicationContext

        var next = false
        updateAppWidgetState(app, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[WidgetKeys.vibrate] ?: Prefs.isVibrateOn(app)
            next = !current
            prefs.toMutablePreferences().apply { this[WidgetKeys.vibrate] = next }
        }

        Prefs.setVibrate(app, next, blocking = true)

        if (next) {
            PrayerAlarmScheduler.cancelAll(app)
            PrayerAlarmScheduler.scheduleSixFromPrefs(app)
        } else {
            PrayerAlarmScheduler.cancelAll(app)
        }

        val mgr = GlanceAppWidgetManager(app)
        val ids = mgr.getGlanceIds(MyAppWidget::class.java)
        val widget = MyAppWidget()
        ids.forEach { widget.update(app, it) }
    }
}
