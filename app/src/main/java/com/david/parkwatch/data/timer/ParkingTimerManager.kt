package com.david.parkwatch.data.timer

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.david.parkwatch.R

object ParkingTimerManager {
    const val CHANNEL_ID = "parkwatch_timer_channel"
    const val NOTIFICATION_ID = 4001

    const val ACTION_TIMER_WARNING = "com.david.parkwatch.ACTION_TIMER_WARNING"
    const val ACTION_TIMER_EXPIRED = "com.david.parkwatch.ACTION_TIMER_EXPIRED"

    private const val REQUEST_CODE_WARNING = 1001
    private const val REQUEST_CODE_EXPIRED = 1002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.timer_channel_name)
            val descriptionText = context.getString(R.string.timer_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun scheduleTimer(context: Context, expiresAt: Long) {
        createNotificationChannel(context)
        cancelTimer(context)

        val now = System.currentTimeMillis()
        if (expiresAt <= now) return

        val durationRemaining = expiresAt - now
        val fifteenMinutesMillis = 15 * 60 * 1000L

        // If there's more than 20 minutes remaining, schedule a 15-minute advance warning
        if (durationRemaining > 20 * 60 * 1000L) {
            val warningTime = expiresAt - fifteenMinutesMillis
            scheduleAlarm(context, warningTime, REQUEST_CODE_WARNING, ACTION_TIMER_WARNING)
        }

        // Schedule exact expiration alarm
        scheduleAlarm(context, expiresAt, REQUEST_CODE_EXPIRED, ACTION_TIMER_EXPIRED)
    }

    fun cancelTimer(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val warningIntent = Intent(context, ParkingTimerReceiver::class.java).apply {
            action = ACTION_TIMER_WARNING
        }
        val warningPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_WARNING,
            warningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(warningPendingIntent)
        warningPendingIntent.cancel()

        val expiredIntent = Intent(context, ParkingTimerReceiver::class.java).apply {
            action = ACTION_TIMER_EXPIRED
        }
        val expiredPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_EXPIRED,
            expiredIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(expiredPendingIntent)
        expiredPendingIntent.cancel()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        requestCode: Int,
        action: String,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ParkingTimerReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}

