package com.david.parkwatch.presentation.util

import android.content.Context
import com.david.parkwatch.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormatter {
    fun formatExpireTime(expiresAt: Long): String {
        return SimpleDateFormat("h:mm a", Locale.US).format(Date(expiresAt))
    }

    fun formatElapsedTime(parkedAt: Long, context: Context): String {
        val now = System.currentTimeMillis()
        val diffMs = (now - parkedAt).coerceAtLeast(0L)
        val minutes = (diffMs / 60_000L).toInt()
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> context.getString(R.string.detail_time_just_now)
            minutes < 60 -> context.getString(R.string.detail_time_minutes, minutes)
            hours < 24 -> {
                val remMin = minutes % 60
                if (remMin == 0) {
                    context.getString(R.string.detail_time_hours, hours)
                } else {
                    context.getString(R.string.detail_time_hours_minutes, hours, remMin)
                }
            }
            else -> context.getString(R.string.detail_time_days, days)
        }
    }
}

