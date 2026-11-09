package com.david.parkwatch.data.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.presentation.complication.ParkWatchComplicationManager
import com.david.parkwatch.presentation.tile.ParkWatchTileManager

/**
 * Recovers parking timer alarms and refreshes Wear OS tiles/complications
 * after a smartwatch reboot (BOOT_COMPLETED) or app update (MY_PACKAGE_REPLACED).
 */
class ParkingBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }

        // 1. Refresh Tiles and Complications after system boot
        try {
            ParkWatchTileManager.requestUpdate(context)
            ParkWatchComplicationManager.requestUpdate(context)
        } catch (_: Exception) {
        }

        // 2. Recover active parking timers and ongoing notification
        val repository = ParkingRepository(context)
        val location = repository.getParkingLocation() ?: return
        com.david.parkwatch.presentation.notification.ParkingNotificationManager.showParkingNotification(context, location)
        val expiresAt = location.expiresAt ?: return

        val now = System.currentTimeMillis()
        if (expiresAt > now) {
            // Re-arm alarms for the remaining duration
            ParkingTimerManager.scheduleTimer(context, expiresAt)
        } else {
            // Timer expired while the watch was off: post expired notification immediately
            val expiredIntent = Intent(context, ParkingTimerReceiver::class.java).apply {
                this.action = ParkingTimerManager.ACTION_TIMER_EXPIRED
            }
            context.sendBroadcast(expiredIntent)
        }
    }
}
