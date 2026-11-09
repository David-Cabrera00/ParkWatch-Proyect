package com.david.parkwatch.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class CompassSensorManagerTest {

    private lateinit var context: Context
    private lateinit var sensorManager: SensorManager
    private lateinit var compassSensorManager: CompassSensorManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        compassSensorManager = CompassSensorManager(context)
    }

    @Test
    fun startListening_and_stopListening_manageListenersCleanly() {
        var receivedAzimuth: Float? = null
        compassSensorManager.onAzimuthChanged = { azimuth ->
            receivedAzimuth = azimuth
        }

        compassSensorManager.startListening()
        compassSensorManager.stopListening()

        // Verify stopping clears listener state cleanly without crashes
        assertNull(receivedAzimuth)
    }
}
