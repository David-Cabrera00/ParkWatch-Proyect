package com.david.parkwatch.presentation.tile

import android.content.Context
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.tiles.RequestBuilders
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ParkWatchTileServiceTest {

    private lateinit var context: Context
    private lateinit var repository: ParkingRepository
    private lateinit var service: ParkWatchTileService

    private val deviceParams = DeviceParametersBuilders.DeviceParameters.Builder()
        .setScreenWidthDp(384)
        .setScreenHeightDp(384)
        .setScreenShape(DeviceParametersBuilders.SCREEN_SHAPE_ROUND)
        .build()

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("parkwatch_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = ParkingRepository(context)
        service = Robolectric.buildService(ParkWatchTileService::class.java).create().get()
    }

    @Test
    fun onTileRequest_whenNoParkingSaved_buildsEmptyTile() {
        val request = RequestBuilders.TileRequest.Builder()
            .setDeviceConfiguration(deviceParams)
            .build()

        val tileFuture = service.onTileRequest(request)
        val tile = tileFuture.get()

        assertNotNull(tile)
        assertEquals("1", tile.resourcesVersion)
        assertNotNull(tile.tileTimeline)
    }

    @Test
    fun onTileRequest_whenParkingSaved_buildsParkedTile() {
        val location = ParkingLocation(
            id = UUID.randomUUID().toString(),
            floor = "P2",
            zone = "B",
            row = "10",
            spot = "4",
            parkedAt = System.currentTimeMillis() - 15 * 60 * 1000L,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            vehicleType = VehicleType.CAR,
        )
        repository.saveParkingLocation(location)

        val request = RequestBuilders.TileRequest.Builder()
            .setDeviceConfiguration(deviceParams)
            .build()

        val tileFuture = service.onTileRequest(request)
        val tile = tileFuture.get()

        assertNotNull(tile)
        assertEquals("1", tile.resourcesVersion)
        assertNotNull(tile.tileTimeline)
    }

    @Test
    fun onTileResourcesRequest_returnsResources() {
        val request = RequestBuilders.ResourcesRequest.Builder()
            .setDeviceConfiguration(deviceParams)
            .setVersion("1")
            .build()

        val resourcesFuture = service.onTileResourcesRequest(request)
        val resources = resourcesFuture.get()

        assertNotNull(resources)
        assertEquals("1", resources.version)
    }
}
