package com.david.parkwatch.domain.model

enum class VehicleType {
    CAR,
    MOTORCYCLE,
    BICYCLE,
    SCOOTER;

    companion object {
        fun fromString(value: String?): VehicleType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CAR
        }
    }
}

