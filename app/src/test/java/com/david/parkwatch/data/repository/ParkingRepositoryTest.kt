package com.david.parkwatch.data.repository

import android.content.Context
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ParkingRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: ParkingRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = ParkingRepository(context)
    }

    @Test
    fun getParkingLocation_returnsNull_whenNothingWasSaved() {
        assertNull(repository.getParkingLocation())
    }

    @Test
    fun saveParkingLocation_roundTripsAllFields() {
        val location = createLocation(
            floor = "P2",
            zone = "B",
            row = "14",
            spot = "27",
            latitude = 40.416775,
            longitude = -3.703790,
        )

        repository.saveParkingLocation(location)
        val saved = repository.getParkingLocation()

        assertEquals(location.id, saved?.id)
        assertEquals("P2", saved?.floor)
        assertEquals("B", saved?.zone)
        assertEquals("14", saved?.row)
        assertEquals("27", saved?.spot)
        assertEquals(40.416775, saved?.latitude ?: 0.0, COORDINATE_DELTA)
        assertEquals(-3.703790, saved?.longitude ?: 0.0, COORDINATE_DELTA)
        assertEquals(VehicleType.CAR, saved?.vehicleType)
        assertEquals(location.parkedAt, saved?.parkedAt)
        assertEquals(location.createdAt, saved?.createdAt)
        assertEquals(location.updatedAt, saved?.updatedAt)
    }

    @Test
    fun saveParkingLocation_keepsOptionalFieldsNull() {
        repository.saveParkingLocation(createLocation(spot = null))

        val saved = repository.getParkingLocation()

        assertEquals("P1", saved?.floor)
        assertNull(saved?.spot)
        assertNull(saved?.latitude)
        assertNull(saved?.longitude)
    }

    @Test
    fun saveParkingLocation_replacesThePreviousLocation() {
        repository.saveParkingLocation(createLocation(floor = "P1", zone = "A"))
        repository.saveParkingLocation(
            createLocation(
                id = "second-id",
                floor = "B2",
                zone = "D",
                row = "3",
            )
        )

        val saved = repository.getParkingLocation()

        assertEquals("second-id", saved?.id)
        assertEquals("B2", saved?.floor)
        assertEquals("D", saved?.zone)
        assertEquals("3", saved?.row)
    }

    @Test
    fun saveEditAndDeleteFlow_persistsEachStep() {
        val original = createLocation(floor = "P1", zone = "A", row = "1")
        repository.saveParkingLocation(original)
        assertEquals("P1", repository.getParkingLocation()?.floor)

        val edited = original.copy(
            floor = "P3",
            zone = "C",
            row = "8",
            spot = "12",
            updatedAt = original.updatedAt + 60_000L,
        )
        repository.saveParkingLocation(edited)

        val afterEdit = repository.getParkingLocation()
        assertEquals("P3", afterEdit?.floor)
        assertEquals("C", afterEdit?.zone)
        assertEquals("12", afterEdit?.spot)
        assertEquals(edited.updatedAt, afterEdit?.updatedAt)

        repository.clearParkingLocation()

        assertNull(repository.getParkingLocation())
    }

    @Test
    fun saveParkingLocation_persistsAndClearsExpiresAt() {
        val expiresAtTimestamp = 1_700_003_600_000L
        val locationWithTimer = createLocation(expiresAt = expiresAtTimestamp)
        repository.saveParkingLocation(locationWithTimer)

        val saved = repository.getParkingLocation()
        assertEquals(expiresAtTimestamp, saved?.expiresAt)

        val locationWithoutTimer = locationWithTimer.copy(expiresAt = null)
        repository.saveParkingLocation(locationWithoutTimer)

        val updated = repository.getParkingLocation()
        assertNull(updated?.expiresAt)
    }

    @Test
    fun saveParkingLocation_persistsDifferentVehicleTypes() {
        VehicleType.entries.forEach { type ->
            repository.saveParkingLocation(createLocation(vehicleType = type))
            val saved = repository.getParkingLocation()
            assertEquals(type, saved?.vehicleType)
        }
    }

    private fun createLocation(
        id: String = "test-id",
        floor: String = "P1",
        zone: String = "A",
        row: String = "1",
        spot: String? = "1",
        latitude: Double? = null,
        longitude: Double? = null,
        expiresAt: Long? = null,
        vehicleType: VehicleType = VehicleType.CAR,
    ) = ParkingLocation(
        id = id,
        floor = floor,
        zone = zone,
        row = row,
        spot = spot,
        latitude = latitude,
        longitude = longitude,
        parkedAt = 1_700_000_000_000L,
        expiresAt = expiresAt,
        vehicleType = vehicleType,
        createdAt = 1_700_000_000_000L,
        updatedAt = 1_700_000_000_000L,
    )

    private companion object {
        const val PREFERENCES_FILE = "parkwatch_prefs"
        const val COORDINATE_DELTA = 0.0001
    }
}
