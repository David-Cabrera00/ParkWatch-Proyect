# ParkWatch — Data Model

## ParkingLocation

Represents the vehicle's currently saved parking location.

### Properties

| Property | Type | Required | Description |
|---|---|---|---|
| id | String | Yes | Unique identifier |
| floor | String | Yes | Parking floor |
| zone | String | Yes | Parking zone |
| row | String | Yes | Parking row |
| spot | String? | No | Optional parking spot |
| latitude | Double? | No | Optional GPS latitude |
| longitude | Double? | No | Optional GPS longitude |
| parkedAt | Long | Yes | Timestamp when the location was saved |
| createdAt | Long | Yes | Creation timestamp |
| updatedAt | Long | Yes | Last modification timestamp |

---

## Kotlin Domain Representation

```kotlin
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
    val updatedAt: Long
)