package com.david.parkwatch.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.VehicleType

val VehicleType.iconRes: Int
    @DrawableRes
    get() = when (this) {
        VehicleType.CAR -> R.drawable.ic_directions_car
        VehicleType.MOTORCYCLE -> R.drawable.ic_two_wheeler
        VehicleType.BICYCLE -> R.drawable.ic_pedal_bike
        VehicleType.SCOOTER -> R.drawable.ic_electric_scooter
    }

val VehicleType.labelRes: Int
    @StringRes
    get() = when (this) {
        VehicleType.CAR -> R.string.vehicle_type_car
        VehicleType.MOTORCYCLE -> R.string.vehicle_type_motorcycle
        VehicleType.BICYCLE -> R.string.vehicle_type_bicycle
        VehicleType.SCOOTER -> R.string.vehicle_type_scooter
    }

val VehicleType.emoji: String
    get() = when (this) {
        VehicleType.CAR -> "🚗"
        VehicleType.MOTORCYCLE -> "🏍️"
        VehicleType.BICYCLE -> "🚲"
        VehicleType.SCOOTER -> "🛴"
    }

