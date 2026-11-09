package com.david.parkwatch.presentation.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.MainActivity
import com.david.parkwatch.presentation.model.iconRes
import com.david.parkwatch.presentation.model.labelRes

object ParkingNotificationManager {
    const val CHANNEL_ID = "parkwatch_parking_status"
    const val NOTIFICATION_ID = 2001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.home_saved_title),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = context.getString(R.string.home_saved_title)
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showParkingNotification(context: Context, location: ParkingLocation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return
        }

        createNotificationChannel(context)

        val vehicleName = context.getString(location.vehicleType.labelRes)
        val title = "${context.getString(R.string.home_saved_title)} ($vehicleName)"
        val contentText = buildString {
            append("${location.floor} · ${location.zone} · ${location.row}")
            if (!location.spot.isNullOrBlank()) {
                append(" · ${location.spot}")
            }
        }

        val detailIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_DESTINATION, MainActivity.DESTINATION_DETAIL)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val detailPendingIntent = PendingIntent.getActivity(
            context,
            101,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(location.vehicleType.iconRes)
            .setContentTitle(title)
            .setContentText(contentText)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(detailPendingIntent)
            .setAutoCancel(false)

        // Action: Guiarme con brújula si hay GPS
        if (location.latitude != null && location.longitude != null) {
            val compassIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_DESTINATION, MainActivity.DESTINATION_COMPASS)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val compassPendingIntent = PendingIntent.getActivity(
                context,
                102,
                compassIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            builder.addAction(
                R.drawable.ic_location_on,
                context.getString(R.string.compass_guide_me),
                compassPendingIntent,
            )
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    fun cancelParkingNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }
}
