package com.david.parkwatch.data.repository

import android.content.Context
import androidx.core.content.edit
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
        val latitude = getStoredDouble("latitude")
        val longitude = getStoredDouble("longitude")
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

    private fun getStoredDouble(key: String): Double? {
        if (!prefs.contains(key)) return null
        return try {
            java.lang.Double.longBitsToDouble(prefs.getLong(key, 0L))
        } catch (_: ClassCastException) {
            try {
                // Migration fallback for legacy Float representation
                prefs.getFloat(key, 0f).toDouble()
            } catch (_: ClassCastException) {
                prefs.getString(key, null)?.toDoubleOrNull()
            }
        }
    }

    fun saveParkingLocation(location: ParkingLocation) {
        prefs.edit {
            putString("id", location.id)
            putString("floor", location.floor)
            putString("zone", location.zone)
            putString("row", location.row)
            putString("spot", location.spot)
            putString("vehicleType", location.vehicleType.name)
            if (location.latitude != null) {
                putLong("latitude", java.lang.Double.doubleToRawLongBits(location.latitude))
            } else {
                remove("latitude")
            }
            if (location.longitude != null) {
                putLong("longitude", java.lang.Double.doubleToRawLongBits(location.longitude))
            } else {
                remove("longitude")
            }
            if (location.expiresAt != null) {
                putLong("expiresAt", location.expiresAt)
            } else {
                remove("expiresAt")
            }
            putLong("parkedAt", location.parkedAt)
            putLong("createdAt", location.createdAt)
            putLong("updatedAt", location.updatedAt)
        }

        addToHistory(location)
    }

    /**
     * Completes and archives the parking session into history, clearing active state.
     */
    fun archiveParkingLocation(location: ParkingLocation) {
        addToHistory(location)
        clearParkingLocation(removeFromHistory = false)
    }

    fun clearParkingLocation(removeFromHistory: Boolean = false) {
        val currentId = prefs.getString("id", null)
        prefs.edit {
            remove("id")
            remove("floor")
            remove("zone")
            remove("row")
            remove("spot")
            remove("latitude")
            remove("longitude")
            remove("parkedAt")
            remove("expiresAt")
            remove("vehicleType")
            remove("createdAt")
            remove("updatedAt")
        }
        if (removeFromHistory && currentId != null) {
            removeHistoryItem(currentId)
        }
    }

    fun getParkingHistory(): List<ParkingLocation> {
        val raw = prefs.getString("parking_history", null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<ParkingLocation>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                if (id.isBlank()) continue
                list.add(
                    ParkingLocation(
                        id = id,
                        floor = obj.optString("floor", "—"),
                        zone = obj.optString("zone", "—"),
                        row = obj.optString("row", "—"),
                        spot = if (obj.has("spot") && !obj.isNull("spot")) obj.optString("spot") else null,
                        latitude = if (obj.has("latitude") && !obj.isNull("latitude")) {
                            val v = obj.optDouble("latitude")
                            if (v.isNaN()) null else v
                        } else null,
                        longitude = if (obj.has("longitude") && !obj.isNull("longitude")) {
                            val v = obj.optDouble("longitude")
                            if (v.isNaN()) null else v
                        } else null,
                        parkedAt = obj.optLong("parkedAt", System.currentTimeMillis()),
                        expiresAt = if (obj.has("expiresAt") && !obj.isNull("expiresAt")) obj.optLong("expiresAt") else null,
                        vehicleType = VehicleType.fromString(obj.optString("vehicleType", "CAR")),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun removeHistoryItem(id: String) {
        val updatedHistory = getParkingHistory().filter { it.id != id }
        saveHistoryList(updatedHistory)
    }

    fun clearParkingHistory() {
        prefs.edit { remove("parking_history") }
    }

    private fun addToHistory(location: ParkingLocation) {
        val history = getParkingHistory().filter { it.id != location.id }.toMutableList()
        history.add(0, location)
        saveHistoryList(history)
    }

    private fun saveHistoryList(list: List<ParkingLocation>) {
        val trimmed = list.take(10)
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
        prefs.edit { putString("parking_history", array.toString()) }
    }
}

