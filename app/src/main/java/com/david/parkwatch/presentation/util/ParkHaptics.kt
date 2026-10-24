package com.david.parkwatch.presentation.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Tactile haptic feedback profiles designed specifically for Wear OS smartwatches.
 * Utilizes amplitude-modulated waveforms on supported hardware (LRA motors).
 */
object ParkHaptics {

    /**
     * Plays a crisp double-pulse confirmation when saving or updating a vehicle location.
     */
    fun playSuccess(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 60, 80, 100)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 60, 80, 100), -1)
            }
        } catch (_: Exception) {
            // Silently fall back if vibrator is unavailable
        }
    }

    /**
     * Plays an energetic celebration pulse when arriving within range of the parked vehicle.
     */
    fun playArrival(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 80, 70, 80, 70, 150)
                val amplitudes = intArrayOf(0, 200, 0, 220, 0, 255)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 80, 70, 80, 70, 150), -1)
            }
        } catch (_: Exception) {
            // Silently fall back if vibrator is unavailable
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}

