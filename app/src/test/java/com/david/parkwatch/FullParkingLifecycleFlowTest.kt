package com.david.parkwatch

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import com.david.parkwatch.data.location.LocationHelper
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.data.timer.ParkingBootReceiver
import com.david.parkwatch.data.timer.ParkingTimerManager
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
class FullParkingLifecycleFlowTest {

    private lateinit var context: Context
    private lateinit var repository: ParkingRepository
    private lateinit var bootReceiver: ParkingBootReceiver
    private lateinit var alarmManager: AlarmManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("parkwatch_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = ParkingRepository(context)
        bootReceiver = ParkingBootReceiver()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Test
    fun completeLifecycle_save_edit_timer_boot_arrival_liberate() {
        // Step 1: Initial Save with high-precision GPS
        val initialLat = 40.416775
        val initialLng = -3.703790
        val startTime = System.currentTimeMillis()

        val location = ParkingLocation(
            id = UUID.randomUUID().toString(),
            floor = "P1",
            zone = "A",
            row = "5",
            spot = "10",
            latitude = initialLat,
            longitude = initialLng,
            parkedAt = startTime,
            createdAt = startTime,
            updatedAt = startTime,
            vehicleType = VehicleType.CAR,
        )
        repository.saveParkingLocation(location)

        val activeLocation = repository.getParkingLocation()
        assertNotNull(activeLocation)
        assertEquals("P1", activeLocation?.floor)
        assertEquals("A", activeLocation?.zone)
        assertEquals(initialLat, activeLocation?.latitude ?: 0.0, 0.000001)

        // Step 2: Edit location details (e.g. user corrected spot to 12)
        val edited = activeLocation!!.copy(
            spot = "12",
            updatedAt = System.currentTimeMillis(),
        )
        repository.saveParkingLocation(edited)
        assertEquals("12", repository.getParkingLocation()?.spot)

        // Step 3: Set parking timer (e.g. 60 minutes)
        val timerDurationMs = 60 * 60 * 1000L
        val expiresAt = System.currentTimeMillis() + timerDurationMs
        val withTimer = edited.copy(expiresAt = expiresAt)
        repository.saveParkingLocation(withTimer)
        ParkingTimerManager.scheduleTimer(context, expiresAt)

        val shadowAm = shadowOf(alarmManager)
        assertNotNull(shadowAm.peekNextScheduledAlarm())

        // Step 4: Simulate device reboot during active timer stay
        val bootIntent = Intent(Intent.ACTION_BOOT_COMPLETED)
        bootReceiver.onReceive(context, bootIntent)
        assertNotNull(shadowAm.peekNextScheduledAlarm())

        // Step 5: Guidance & Arrival check (distance <= 10 meters)
        // User is at 40.416776, -3.703791 (within ~0.15 meters of vehicle)
        val userCurrentLat = 40.416776
        val userCurrentLng = -3.703791
        val (dist, bearing) = LocationHelper.calculateDistanceAndBearing(
            userCurrentLat,
            userCurrentLng,
            initialLat,
            initialLng,
        )
        assertTrue("Distance should be <= 10 meters for arrival", dist <= 10f)

        // Step 6: Liberate parking spot / Finish stay
        ParkingTimerManager.cancelTimer(context)
        repository.archiveParkingLocation(withTimer)

        // Verify active parking is cleared
        assertNull(repository.getParkingLocation())

        // Verify alarm is canceled
        assertNull(shadowAm.peekNextScheduledAlarm())

        // Verify stay is preserved in history
        val history = repository.getParkingHistory()
        assertEquals(1, history.size)
        assertEquals(withTimer.id, history[0].id)
        assertEquals("12", history[0].spot)
    }
}
