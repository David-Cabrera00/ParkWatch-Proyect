package com.david.parkwatch.presentation.tile

import android.content.Context
import androidx.concurrent.futures.ResolvableFuture
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.david.parkwatch.R
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.model.labelRes
import com.david.parkwatch.presentation.MainActivity
import com.david.parkwatch.presentation.util.TimeFormatter
import com.google.common.util.concurrent.ListenableFuture

private const val RESOURCES_VERSION = "1"
private const val COLOR_EMERALD = 0xFF2EBD6E.toInt()
private const val COLOR_ERROR = 0xFFFF5252.toInt()
private const val COLOR_TEXT_PRIMARY = 0xFFFFFFFF.toInt()
private const val COLOR_TEXT_MUTED = 0xFFB0BEC5.toInt()

class ParkWatchTileService : TileService() {
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val future = ResolvableFuture.create<TileBuilders.Tile>()
        val repository = ParkingRepository(this)
        val location = repository.getParkingLocation()

        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder()
                            .setLayout(
                                LayoutElementBuilders.Layout.Builder()
                                    .setRoot(buildLayout(this, requestParams.deviceConfiguration, location))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        future.set(tile)
        return future
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        val future = ResolvableFuture.create<ResourceBuilders.Resources>()
        val resources = ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .build()
        future.set(resources)
        return future
    }

    private fun createLaunchClick(context: Context, destination: String): ModifiersBuilders.Clickable {
        val stringExtra = ActionBuilders.AndroidStringExtra.Builder()
            .setValue(destination)
            .build()
        val activity = ActionBuilders.AndroidActivity.Builder()
            .setPackageName(context.packageName)
            .setClassName(MainActivity::class.java.name)
            .addKeyToExtraMapping(MainActivity.EXTRA_DESTINATION, stringExtra)
            .build()
        return ModifiersBuilders.Clickable.Builder()
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(activity)
                    .build()
            )
            .build()
    }

    private fun buildLayout(
        context: Context,
        deviceParams: DeviceParametersBuilders.DeviceParameters,
        location: ParkingLocation?,
    ): LayoutElementBuilders.LayoutElement {
        return if (location != null) {
            val vehicleLabel = context.getString(location.vehicleType.labelRes)
            val mainText = buildString {
                append("${location.floor} · ${location.zone} · ${location.row}")
                if (!location.spot.isNullOrBlank()) {
                    append(" · ${location.spot}")
                }
            }

            val now = System.currentTimeMillis()
            val isExpired = location.expiresAt != null && (location.expiresAt - now) <= 0
            val statusColor = if (isExpired) COLOR_ERROR else COLOR_EMERALD

            val statusText = if (location.expiresAt != null) {
                val remaining = location.expiresAt - now
                if (remaining <= 0) {
                    val expiredMin = ((-remaining) / 60_000L).coerceAtLeast(0L)
                    context.getString(R.string.timer_expired_ago, expiredMin)
                } else {
                    val remMin = (remaining / 60_000L).coerceAtLeast(0L)
                    if (remMin < 60) {
                        context.getString(R.string.timer_remaining_minutes, remMin)
                    } else {
                        context.getString(R.string.timer_remaining_hours_minutes, remMin / 60, remMin % 60)
                    }
                }
            } else {
                TimeFormatter.formatElapsedTime(location.parkedAt, context)
            }

            val hasGps = location.latitude != null && location.longitude != null
            val chipLabel = if (hasGps) {
                context.getString(R.string.compass_guide_me)
            } else {
                context.getString(R.string.tile_view_action)
            }
            val chipDestination = if (hasGps) {
                MainActivity.DESTINATION_COMPASS
            } else {
                MainActivity.DESTINATION_DETAIL
            }

            PrimaryLayout.Builder(deviceParams)
                .setPrimaryLabelTextContent(
                    Text.Builder(context, "${context.getString(R.string.home_saved_title)} ($vehicleLabel)")
                        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                        .setColor(argb(COLOR_EMERALD))
                        .build()
                )
                .setContent(
                    LayoutElementBuilders.Column.Builder()
                        .addContent(
                            Text.Builder(context, mainText)
                                .setTypography(Typography.TYPOGRAPHY_TITLE2)
                                .setColor(argb(COLOR_TEXT_PRIMARY))
                                .setMaxLines(2)
                                .build()
                        )
                        .addContent(
                            Text.Builder(context, statusText)
                                .setTypography(Typography.TYPOGRAPHY_BODY2)
                                .setColor(argb(statusColor))
                                .setMaxLines(1)
                                .build()
                        )
                        .build()
                )
                .setPrimaryChipContent(
                    CompactChip.Builder(
                        context,
                        chipLabel,
                        createLaunchClick(context, chipDestination),
                        deviceParams,
                    ).build()
                )
                .build()
        } else {
            PrimaryLayout.Builder(deviceParams)
                .setPrimaryLabelTextContent(
                    Text.Builder(context, context.getString(R.string.tile_name))
                        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                        .setColor(argb(COLOR_EMERALD))
                        .build()
                )
                .setContent(
                    Text.Builder(context, context.getString(R.string.tile_empty_title))
                        .setTypography(Typography.TYPOGRAPHY_TITLE3)
                        .setColor(argb(COLOR_TEXT_PRIMARY))
                        .setMaxLines(2)
                        .build()
                )
                .setPrimaryChipContent(
                    CompactChip.Builder(
                        context,
                        context.getString(R.string.tile_empty_action),
                        createLaunchClick(context, MainActivity.DESTINATION_SAVE),
                        deviceParams,
                    ).build()
                )
                .build()
        }
    }
}
