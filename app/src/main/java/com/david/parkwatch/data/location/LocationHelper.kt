package com.david.parkwatch.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat

class LocationHelper(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownCoordinates(): Pair<Double, Double>? {
        if (!hasLocationPermission() || locationManager == null) return null

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || (loc.hasAccuracy() && loc.accuracy < bestLocation.accuracy)) {
                            bestLocation = loc
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }

        return bestLocation?.let { Pair(it.latitude, it.longitude) }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(
        minTimeMs: Long = 1000L,
        minDistanceMeters: Float = 1f,
        onLocationChanged: (Location) -> Unit,
    ): LocationListener? {
        if (!hasLocationPermission() || locationManager == null) return null

        val listener = LocationListener { location ->
            onLocationChanged(location)
        }

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
        )

        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    locationManager.requestLocationUpdates(
                        provider,
                        minTimeMs,
                        minDistanceMeters,
                        listener,
                    )
                }
            } catch (_: Exception) {
            }
        }

        return listener
    }

    fun removeLocationUpdates(listener: LocationListener?) {
        if (listener != null) {
            try {
                locationManager?.removeUpdates(listener)
            } catch (_: Exception) {
            }
        }
    }

    companion object {
        private const val EARTH_RADIUS_METERS = 6371000.0

        fun calculateDistanceAndBearing(
            currentLat: Double,
            currentLng: Double,
            targetLat: Double,
            targetLng: Double,
        ): Pair<Float, Float> {
            val phi1 = Math.toRadians(currentLat)
            val phi2 = Math.toRadians(targetLat)
            val deltaPhi = Math.toRadians(targetLat - currentLat)
            val deltaLambda = Math.toRadians(targetLng - currentLng)

            val a = kotlin.math.sin(deltaPhi / 2.0) * kotlin.math.sin(deltaPhi / 2.0) +
                kotlin.math.cos(phi1) * kotlin.math.cos(phi2) *
                kotlin.math.sin(deltaLambda / 2.0) * kotlin.math.sin(deltaLambda / 2.0)
            val c = 2.0 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1.0 - a))
            val distanceMeters = (EARTH_RADIUS_METERS * c).toFloat()

            val y = kotlin.math.sin(deltaLambda) * kotlin.math.cos(phi2)
            val x = kotlin.math.cos(phi1) * kotlin.math.sin(phi2) -
                kotlin.math.sin(phi1) * kotlin.math.cos(phi2) * kotlin.math.cos(deltaLambda)
            val bearingRad = kotlin.math.atan2(y, x)
            val bearingDeg = ((Math.toDegrees(bearingRad) + 360.0) % 360.0).toFloat()

            return Pair(distanceMeters, bearingDeg)
        }

        fun getCardinalDirectionKey(bearing: Float): String {
            val normalized = (bearing + 360f) % 360f
            return when {
                normalized >= 337.5 || normalized < 22.5 -> "N"
                normalized < 67.5 -> "NE"
                normalized < 112.5 -> "E"
                normalized < 157.5 -> "SE"
                normalized < 202.5 -> "S"
                normalized < 247.5 -> "SW"
                normalized < 292.5 -> "W"
                else -> "NW"
            }
        }
    }
}

