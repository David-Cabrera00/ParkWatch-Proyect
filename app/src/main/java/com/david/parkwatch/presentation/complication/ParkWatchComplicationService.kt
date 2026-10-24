package com.david.parkwatch.presentation.complication

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.MonochromaticImageComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.david.parkwatch.R
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.model.iconRes
import com.david.parkwatch.presentation.model.labelRes
import com.david.parkwatch.presentation.MainActivity

class ParkWatchComplicationService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val dummyLocation = ParkingLocation(
            id = "preview",
            floor = "P2",
            zone = "B",
            row = "14",
            spot = "27",
            parkedAt = System.currentTimeMillis() - 15 * 60_000L,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        return buildComplicationData(type, dummyLocation)
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val repository = ParkingRepository(this)
        val location = repository.getParkingLocation()
        return buildComplicationData(request.complicationType, location)
    }

    private fun buildComplicationData(
        type: ComplicationType,
        location: ParkingLocation?,
    ): ComplicationData? {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val tapAction = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val vehicleIconRes = location?.vehicleType?.iconRes ?: R.drawable.ic_directions_car
        val vehicleIcon = MonochromaticImage.Builder(
            Icon.createWithResource(this, vehicleIconRes),
        ).build()

        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                if (location != null) {
                    val text = location.floor
                    val title = location.zone
                    ShortTextComplicationData.Builder(
                        text = PlainComplicationText.Builder(text).build(),
                        contentDescription = PlainComplicationText.Builder(getString(R.string.home_saved_title)).build(),
                    )
                        .setTitle(PlainComplicationText.Builder(title).build())
                        .setMonochromaticImage(vehicleIcon)
                        .setTapAction(tapAction)
                        .build()
                } else {
                    ShortTextComplicationData.Builder(
                        text = PlainComplicationText.Builder(getString(R.string.complication_empty_text)).build(),
                        contentDescription = PlainComplicationText.Builder(getString(R.string.home_empty_title)).build(),
                    )
                        .setTitle(PlainComplicationText.Builder(getString(R.string.complication_empty_title)).build())
                        .setMonochromaticImage(vehicleIcon)
                        .setTapAction(tapAction)
                        .build()
                }
            }
            ComplicationType.LONG_TEXT -> {
                if (location != null) {
                    val fullLocation = buildString {
                        append("${location.floor} · ${location.zone} · ${location.row}")
                        if (!location.spot.isNullOrBlank()) {
                            append(" · ${location.spot}")
                        }
                    }
                    LongTextComplicationData.Builder(
                        text = PlainComplicationText.Builder(fullLocation).build(),
                        contentDescription = PlainComplicationText.Builder(getString(R.string.home_saved_title)).build(),
                    )
                        .setTitle(PlainComplicationText.Builder(getString(location.vehicleType.labelRes)).build())
                        .setMonochromaticImage(vehicleIcon)
                        .setTapAction(tapAction)
                        .build()
                } else {
                    LongTextComplicationData.Builder(
                        text = PlainComplicationText.Builder(getString(R.string.home_empty_title)).build(),
                        contentDescription = PlainComplicationText.Builder(getString(R.string.home_empty_title)).build(),
                    )
                        .setTitle(PlainComplicationText.Builder(getString(R.string.app_name)).build())
                        .setMonochromaticImage(vehicleIcon)
                        .setTapAction(tapAction)
                        .build()
                }
            }
            ComplicationType.MONOCHROMATIC_IMAGE -> {
                MonochromaticImageComplicationData.Builder(
                    monochromaticImage = vehicleIcon,
                    contentDescription = PlainComplicationText.Builder(getString(R.string.app_name)).build(),
                )
                    .setTapAction(tapAction)
                    .build()
            }
            else -> null
        }
    }
}

