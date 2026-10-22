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
import com.david.parkwatch.presentation.MainActivity
import com.google.common.util.concurrent.ListenableFuture

private const val RESOURCES_VERSION = "1"

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

    private fun buildLayout(
        context: Context,
        deviceParams: DeviceParametersBuilders.DeviceParameters,
        location: ParkingLocation?,
    ): LayoutElementBuilders.LayoutElement {
        val launchAppClick = ModifiersBuilders.Clickable.Builder()
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setClassName(MainActivity::class.java.name)
                            .setPackageName(context.packageName)
                            .build()
                    )
                    .build()
            )
            .build()

        return if (location != null) {
            val mainText = buildString {
                append("${location.floor} · ${location.zone} · ${location.row}")
                if (!location.spot.isNullOrBlank()) {
                    append(" · ${location.spot}")
                }
            }

            val now = System.currentTimeMillis()
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
                val elapsedMinutes = ((now - location.parkedAt) / 60_000L).coerceAtLeast(0L)
                context.getString(R.string.detail_time_minutes, elapsedMinutes)
            }

            PrimaryLayout.Builder(deviceParams)
                .setPrimaryLabelTextContent(
                    Text.Builder(context, context.getString(R.string.home_saved_title))
                        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                        .setColor(argb(0xFF90CAF9.toInt()))
                        .build()
                )
                .setContent(
                    LayoutElementBuilders.Column.Builder()
                        .addContent(
                            Text.Builder(context, mainText)
                                .setTypography(Typography.TYPOGRAPHY_TITLE2)
                                .setColor(argb(0xFFFFFFFF.toInt()))
                                .setMaxLines(2)
                                .build()
                        )
                        .addContent(
                            Text.Builder(context, statusText)
                                .setTypography(Typography.TYPOGRAPHY_BODY2)
                                .setColor(argb(0xFFB0BEC5.toInt()))
                                .setMaxLines(1)
                                .build()
                        )
                        .build()
                )
                .setPrimaryChipContent(
                    CompactChip.Builder(context, context.getString(R.string.tile_view_action), launchAppClick, deviceParams)
                        .build()
                )
                .build()
        } else {
            PrimaryLayout.Builder(deviceParams)
                .setPrimaryLabelTextContent(
                    Text.Builder(context, context.getString(R.string.tile_name))
                        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                        .setColor(argb(0xFF90CAF9.toInt()))
                        .build()
                )
                .setContent(
                    Text.Builder(context, context.getString(R.string.tile_empty_title))
                        .setTypography(Typography.TYPOGRAPHY_TITLE3)
                        .setColor(argb(0xFFFFFFFF.toInt()))
                        .setMaxLines(2)
                        .build()
                )
                .setPrimaryChipContent(
                    CompactChip.Builder(context, context.getString(R.string.tile_empty_action), launchAppClick, deviceParams)
                        .build()
                )
                .build()
        }
    }
}
