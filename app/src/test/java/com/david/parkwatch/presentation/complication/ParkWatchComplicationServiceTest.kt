package com.david.parkwatch.presentation.complication

import android.content.Context
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImageComplicationData
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
class ParkWatchComplicationServiceTest {

    private lateinit var context: Context
    private lateinit var repository: ParkingRepository
    private lateinit var service: ParkWatchComplicationService

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("parkwatch_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = ParkingRepository(context)
        service = Robolectric.buildService(ParkWatchComplicationService::class.java).create().get()
    }

    @Test
    fun getPreviewData_returnsValidComplicationData() {
        val shortText = service.getPreviewData(ComplicationType.SHORT_TEXT)
        assertNotNull(shortText)
        assertTrue(shortText is ShortTextComplicationData)

        val longText = service.getPreviewData(ComplicationType.LONG_TEXT)
        assertNotNull(longText)
        assertTrue(longText is LongTextComplicationData)

        val icon = service.getPreviewData(ComplicationType.MONOCHROMATIC_IMAGE)
        assertNotNull(icon)
        assertTrue(icon is MonochromaticImageComplicationData)

        val ranged = service.getPreviewData(ComplicationType.RANGED_VALUE)
        assertNotNull(ranged)
        assertTrue(ranged is RangedValueComplicationData)
    }

    @Test
    fun onComplicationRequest_whenEmpty_returnsEmptyShortText() = runBlocking {
        val request = ComplicationRequest(101, ComplicationType.SHORT_TEXT, false)
        val data = service.onComplicationRequest(request)

        assertNotNull(data)
        assertTrue(data is ShortTextComplicationData)
    }

    @Test
    fun onComplicationRequest_whenParked_returnsFloorAndZone() = runBlocking {
        val location = ParkingLocation(
            id = UUID.randomUUID().toString(),
            floor = "P3",
            zone = "C",
            row = "8",
            parkedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            vehicleType = VehicleType.MOTORCYCLE,
        )
        repository.saveParkingLocation(location)

        val request = ComplicationRequest(102, ComplicationType.SHORT_TEXT, false)
        val data = service.onComplicationRequest(request)

        assertNotNull(data)
        assertTrue(data is ShortTextComplicationData)
    }
}
