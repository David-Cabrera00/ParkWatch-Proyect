package com.david.parkwatch.data.timer

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ParkingTimerReceiverTest {

    private lateinit var context: Context
    private lateinit var repository: ParkingRepository
    private lateinit var receiver: ParkingTimerReceiver
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("parkwatch_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = ParkingRepository(context)
        receiver = ParkingTimerReceiver()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Test
    fun onReceive_whenNoActiveParking_doesNotPostNotification() {
        val intent = Intent(ParkingTimerManager.ACTION_TIMER_WARNING)
        receiver.onReceive(context, intent)

        val shadowNm = shadowOf(notificationManager)
        assertEquals(0, shadowNm.allNotifications.size)
    }

    @Test
    fun onReceive_whenWarningAction_postsWarningNotification() {
        val location = createSavedLocation()
        repository.saveParkingLocation(location)

        val intent = Intent(ParkingTimerManager.ACTION_TIMER_WARNING)
        receiver.onReceive(context, intent)

        val shadowNm = shadowOf(notificationManager)
        val notifications = shadowNm.allNotifications
        assertEquals(1, notifications.size)

        val notification = notifications[0]
        assertNotNull(notification)
        assertEquals(ParkingTimerManager.CHANNEL_ID, notification.channelId)
    }

    @Test
    fun onReceive_whenExpiredAction_postsExpiredNotification() {
        val location = createSavedLocation()
        repository.saveParkingLocation(location)

        val intent = Intent(ParkingTimerManager.ACTION_TIMER_EXPIRED)
        receiver.onReceive(context, intent)

        val shadowNm = shadowOf(notificationManager)
        val notifications = shadowNm.allNotifications
        assertEquals(1, notifications.size)

        val notification = notifications[0]
        assertNotNull(notification)
        assertEquals(ParkingTimerManager.CHANNEL_ID, notification.channelId)
    }

    private fun createSavedLocation() = ParkingLocation(
        id = UUID.randomUUID().toString(),
        floor = "P2",
        zone = "B",
        row = "14",
        spot = "20",
        parkedAt = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        vehicleType = VehicleType.CAR,
    )
}
