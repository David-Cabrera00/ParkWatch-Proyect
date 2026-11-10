package com.david.parkwatch.data.timer

import android.app.AlarmManager
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
class ParkingBootReceiverTest {

    private lateinit var context: Context
    private lateinit var repository: ParkingRepository
    private lateinit var receiver: ParkingBootReceiver

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("parkwatch_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = ParkingRepository(context)
        receiver = ParkingBootReceiver()
    }

    @Test
    fun onReceive_ignoresIrrelevantAction() {
        val intent = Intent("com.example.IRRELEVANT_ACTION")
        receiver.onReceive(context, intent)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = shadowOf(alarmManager)
        assertNull(shadowAlarmManager.peekNextScheduledAlarm())
    }

    @Test
    fun onReceive_whenNoLocationSaved_doesNotScheduleAlarm() {
        val intent = Intent(Intent.ACTION_BOOT_COMPLETED)
        receiver.onReceive(context, intent)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = shadowOf(alarmManager)
        assertNull(shadowAlarmManager.peekNextScheduledAlarm())
    }

    @Test
    fun onReceive_whenActiveTimerExists_reschedulesAlarm() {
        val futureTime = System.currentTimeMillis() + 60 * 60 * 1000L
        val location = ParkingLocation(
            id = UUID.randomUUID().toString(),
            floor = "P1",
            zone = "A",
            row = "3",
            expiresAt = futureTime,
            parkedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            vehicleType = VehicleType.CAR,
        )
        repository.saveParkingLocation(location)

        val intent = Intent(Intent.ACTION_BOOT_COMPLETED)
        receiver.onReceive(context, intent)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = shadowOf(alarmManager)
        assertNotNull(shadowAlarmManager.peekNextScheduledAlarm())
    }

    @Test
    fun onReceive_whenTimerAlreadyExpired_sendsExpiredBroadcast() {
        val pastTime = System.currentTimeMillis() - 5 * 60 * 1000L
        val location = ParkingLocation(
            id = UUID.randomUUID().toString(),
            floor = "P1",
            zone = "A",
            row = "3",
            expiresAt = pastTime,
            parkedAt = pastTime - 30 * 60 * 1000L,
            createdAt = pastTime,
            updatedAt = pastTime,
            vehicleType = VehicleType.CAR,
        )
        repository.saveParkingLocation(location)

        val intent = Intent(Intent.ACTION_MY_PACKAGE_REPLACED)
        receiver.onReceive(context, intent)

        val shadowApp = shadowOf(RuntimeEnvironment.getApplication())
        val broadcastIntents = shadowApp.broadcastIntents
        val expiredBroadcast = broadcastIntents.firstOrNull {
            it.action == ParkingTimerManager.ACTION_TIMER_EXPIRED
        }
        assertNotNull(expiredBroadcast)
    }
}
