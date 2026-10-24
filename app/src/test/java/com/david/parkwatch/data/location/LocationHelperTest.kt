package com.david.parkwatch.data.location

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationHelperTest {

    @Test
    fun calculateDistanceAndBearing_sameCoordinates_returnsZeroDistance() {
        val (dist, bearing) = LocationHelper.calculateDistanceAndBearing(
            currentLat = 40.416775,
            currentLng = -3.703790,
            targetLat = 40.416775,
            targetLng = -3.703790,
        )

        assertEquals(0f, dist, 0.01f)
    }

    @Test
    fun calculateDistanceAndBearing_northMovement_bearingAroundZero() {
        // Target is slightly north
        val (dist, bearing) = LocationHelper.calculateDistanceAndBearing(
            currentLat = 40.416775,
            currentLng = -3.703790,
            targetLat = 40.417775,
            targetLng = -3.703790,
        )

        assertTrue("Distance should be around 111m, was $dist", dist in 100f..120f)
        assertTrue("Bearing should be approx 0 (North), was $bearing", bearing in 355f..360f || bearing in 0f..5f)
        assertEquals("N", LocationHelper.getCardinalDirectionKey(bearing))
    }

    @Test
    fun calculateDistanceAndBearing_eastMovement_bearingAround90() {
        // Target is slightly east
        val (dist, bearing) = LocationHelper.calculateDistanceAndBearing(
            currentLat = 40.416775,
            currentLng = -3.703790,
            targetLat = 40.416775,
            targetLng = -3.702790,
        )

        assertTrue("Distance should be > 0, was $dist", dist > 50f)
        assertTrue("Bearing should be approx 90 (East), was $bearing", bearing in 85f..95f)
        assertEquals("E", LocationHelper.getCardinalDirectionKey(bearing))
    }

    @Test
    fun getCardinalDirectionKey_allQuadrants_returnsCorrectKey() {
        assertEquals("N", LocationHelper.getCardinalDirectionKey(0f))
        assertEquals("N", LocationHelper.getCardinalDirectionKey(355f))
        assertEquals("N", LocationHelper.getCardinalDirectionKey(10f))
        assertEquals("NE", LocationHelper.getCardinalDirectionKey(45f))
        assertEquals("E", LocationHelper.getCardinalDirectionKey(90f))
        assertEquals("SE", LocationHelper.getCardinalDirectionKey(135f))
        assertEquals("S", LocationHelper.getCardinalDirectionKey(180f))
        assertEquals("SW", LocationHelper.getCardinalDirectionKey(225f))
        assertEquals("W", LocationHelper.getCardinalDirectionKey(270f))
        assertEquals("NW", LocationHelper.getCardinalDirectionKey(315f))
    }
}
