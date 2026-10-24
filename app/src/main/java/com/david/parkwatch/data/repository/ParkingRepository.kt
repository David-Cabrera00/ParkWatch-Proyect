package com.david.parkwatch.data.repository

import android.content.Context
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import org.json.JSONArray
import org.json.JSONObject

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

        addToHistory(location)
    }

    fun clearParkingLocation() {
        prefs.edit()
            .remove("id")
            .remove("floor")
            .remove("zone")
            .remove("row")
            .remove("spot")
            .remove("latitude")
            .remove("longitude")
            .remove("parkedAt")
            .remove("expiresAt")
            .remove("vehicleType")
            .remove("createdAt")
            .remove("updatedAt")
            .apply()
    }

    fun getParkingHistory(): List<ParkingLocation> {
        val raw = prefs.getString("parking_history", null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<ParkingLocation>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ParkingLocation(
                        id = obj.getString("id"),
                        floor = obj.getString("floor"),
                        zone = obj.getString("zone"),
                        row = obj.getString("row"),
                        spot = if (obj.has("spot") && !obj.isNull("spot")) obj.getString("spot") else null,
                        latitude = if (obj.has("latitude") && !obj.isNull("latitude")) obj.getDouble("latitude") else null,
                        longitude = if (obj.has("longitude") && !obj.isNull("longitude")) obj.getDouble("longitude") else null,
                        parkedAt = obj.optLong("parkedAt", 0L),
                        expiresAt = if (obj.has("expiresAt") && !obj.isNull("expiresAt")) obj.getLong("expiresAt") else null,
                        vehicleType = VehicleType.fromString(obj.optString("vehicleType", "CAR")),
                        createdAt = obj.optLong("createdAt", 0L),
                        updatedAt = obj.optLong("updatedAt", 0L),
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clearParkingHistory() {
        prefs.edit().remove("parking_history").apply()
    }

    private fun addToHistory(location: ParkingLocation) {
        val history = getParkingHistory().filter { it.id != location.id }.toMutableList()
        history.add(0, location)
        val trimmed = history.take(10)
        val array = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("floor", item.floor)
                put("zone", item.zone)
                put("row", item.row)
                put("spot", item.spot)
                put("latitude", item.latitude)
                put("longitude", item.longitude)
                put("parkedAt", item.parkedAt)
                put("expiresAt", item.expiresAt)
                put("vehicleType", item.vehicleType.name)
                put("createdAt", item.createdAt)
                put("updatedAt", item.updatedAt)
            }
            array.put(obj)
        }
        prefs.edit().putString("parking_history", array.toString()).apply()
    }
}

