package com.david.parkwatch.domain.model

data class ParkingLocation(
    val id: String,
    val floor: String,
    val zone: String,
    val row: String,
    val spot: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val parkedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)

