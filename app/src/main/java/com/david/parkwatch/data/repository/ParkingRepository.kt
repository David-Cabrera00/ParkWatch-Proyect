package com.david.parkwatch.data.repository

import android.content.Context
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType

class ParkingRepository(context: Context) {
    private val prefs = context.getSharedPreferences("parkwatch_prefs", Context.MODE_PRIVATE)

    fun getParkingLocation(): ParkingLocation? {
        val id = prefs.getString("id", null) ?: return null
        val floor = prefs.getString("floor", null) ?: return null
        val zone = prefs.getString("zone", null) ?: return null
        val row = prefs.getString("row", null) ?: return null
        val spot = prefs.getString("spot", null)
        val latitude = if (prefs.contains("latitude")) prefs.getFloat("latitude", 0f).toDouble() else null
        val longitude = if (prefs.contains("longitude")) prefs.getFloat("longitude", 0f).toDouble() else null
        val parkedAt = prefs.getLong("parkedAt", 0L)
        val expiresAt = if (prefs.contains("expiresAt")) prefs.getLong("expiresAt", 0L) else null
        val vehicleType = VehicleType.fromString(prefs.getString("vehicleType", null))
        val createdAt = prefs.getLong("createdAt", 0L)
        val updatedAt = prefs.getLong("updatedAt", 0L)

        return ParkingLocation(
            id = id,
            floor = floor,
            zone = zone,
            row = row,
            spot = spot,
            latitude = latitude,
            longitude = longitude,
            parkedAt = parkedAt,
            expiresAt = expiresAt,
            vehicleType = vehicleType,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    fun saveParkingLocation(location: ParkingLocation) {
        prefs.edit()
            .putString("id", location.id)
            .putString("floor", location.floor)
            .putString("zone", location.zone)
            .putString("row", location.row)
            .putString("spot", location.spot)
            .putString("vehicleType", location.vehicleType.name)
            .apply {
                if (location.latitude != null) {
                    putFloat("latitude", location.latitude.toFloat())
                } else {
                    remove("latitude")
                }
                if (location.longitude != null) {
                    putFloat("longitude", location.longitude.toFloat())
                } else {
                    remove("longitude")
                }
                if (location.expiresAt != null) {
                    putLong("expiresAt", location.expiresAt)
                } else {
                    remove("expiresAt")
                }
            }
            .putLong("parkedAt", location.parkedAt)
            .putLong("createdAt", location.createdAt)
            .putLong("updatedAt", location.updatedAt)
            .apply()
    }

    fun clearParkingLocation() {
        prefs.edit().clear().apply()
    }
}

