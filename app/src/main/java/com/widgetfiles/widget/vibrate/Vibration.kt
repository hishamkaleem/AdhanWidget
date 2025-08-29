package com.widgetfiles.widget.vibrate

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.media.AudioAttributes
import android.os.VibrationAttributes

object Vibrations {
    fun strongFiveSecondsPattern(context: Context) {
        val vib = vibrator(context) ?: return
        val timings = LongArray(1 + 10 * 2) { i ->
            when {
                i == 0 -> 0L
                i % 2 == 1 -> 400L
                else -> 100L
            }
        }

        val effect = if (Build.VERSION.SDK_INT >= 26) {
            val hasAmp = vib.hasAmplitudeControl()
            if (hasAmp) {
                val amps = IntArray(timings.size) { idx ->
                    if (idx == 0) 0 else if (idx % 2 == 1) 255 else 0
                }
                VibrationEffect.createWaveform(timings, amps, -1)
            } else {
                VibrationEffect.createWaveform(timings, -1)
            }
        } else {
            null
        }

        when {
            Build.VERSION.SDK_INT >= 33 && effect != null -> {
                val attrs = VibrationAttributes.Builder()
                    .setUsage(VibrationAttributes.USAGE_ALARM)
                    .build()
                vib.vibrate(effect, attrs)
            }
            Build.VERSION.SDK_INT >= 26 && effect != null -> {
                @Suppress("DEPRECATION")
                val audio = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                @Suppress("DEPRECATION")
                vib.vibrate(effect, audio)
            }
            else -> {
                @Suppress("DEPRECATION")
                vib.vibrate(timings, -1)
            }
        }
    }

    private fun vibrator(ctx: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= 31)
            ctx.getSystemService(VibratorManager::class.java)?.defaultVibrator
        else
            @Suppress("DEPRECATION")
            ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}
