package com.david.parkwatch.data.timer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.david.parkwatch.R
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.presentation.MainActivity

class ParkingTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val repository = ParkingRepository(context)
        val location = repository.getParkingLocation() ?: return

        ParkingTimerManager.createNotificationChannel(context)

        val isWarning = action == ParkingTimerManager.ACTION_TIMER_WARNING
        val titleRes = if (isWarning) {
            R.string.timer_notification_warning_title
        } else {
            R.string.timer_notification_expired_title
        }
        val textRes = if (isWarning) {
            R.string.timer_notification_warning_text
        } else {
            R.string.timer_notification_expired_text
        }

        val locationDetails = buildString {
            append("${context.getString(R.string.save_parking_floor_label)}: ${location.floor}, ")
            append("${context.getString(R.string.save_parking_zone_label)}: ${location.zone}, ")
            append("${context.getString(R.string.save_parking_row_label)}: ${location.row}")
            if (!location.spot.isNullOrBlank()) {
                append(", ${context.getString(R.string.save_parking_spot_label)}: ${location.spot}")
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val vibrationPattern = if (isWarning) {
            longArrayOf(0, 250, 150, 250) // Double pulse warning (15 min prior)
        } else {
            longArrayOf(0, 500, 150, 500, 150, 800) // Energetic triple pulse expired alarm
        }

        val notification = NotificationCompat.Builder(context, ParkingTimerManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_directions_car)
            .setContentTitle(context.getString(titleRes))
            .setContentText(locationDetails)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${context.getString(textRes)}\n$locationDetails")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVibrate(vibrationPattern)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(ParkingTimerManager.NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Permission might be revoked by user
        }
    }
}

