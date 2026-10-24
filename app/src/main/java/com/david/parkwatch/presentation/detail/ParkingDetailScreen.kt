package com.david.parkwatch.presentation.detail

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.component.ParkDestructiveButton
import com.david.parkwatch.presentation.component.ParkLocationCard
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.component.parkRotaryScroll
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing
import com.david.parkwatch.presentation.util.TimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ParkingDetailScreen(
    location: ParkingLocation,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onCompassClick: () -> Unit = {},
    onSetTimerClick: () -> Unit = {},
    onDeleteConfirmed: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val listState = rememberScalingLazyListState()
    val context = LocalContext.current
    val isConfirmingDelete = remember { mutableStateOf(false) }

    BackHandler {
        if (isConfirmingDelete.value) {
            isConfirmingDelete.value = false
        } else {
            onNavigateBack()
        }
    }

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .parkRotaryScroll(listState),
            contentPadding = PaddingValues(
                top = 28.dp,
                bottom = 48.dp,
                start = 14.dp,
                end = 14.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.SpaceXs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!isConfirmingDelete.value) {
                item {
                    Text(
                        text = stringResource(R.string.detail_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    ParkLocationCard(
                        floor = location.floor,
                        zone = location.zone,
                        row = location.row,
                        spot = location.spot,
                        vehicleType = location.vehicleType,
                        elapsedTime = TimeFormatter.formatElapsedTime(location.parkedAt, context),
                    )
                }
                if (location.latitude != null && location.longitude != null) {
                    item {
                        Text(
                            text = stringResource(R.string.save_parking_gps_captured),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    item {
                        ParkPrimaryButton(
                            onClick = onCompassClick,
                            label = stringResource(R.string.compass_guide_me),
                        )
                    }
                    item {
                        ParkSecondaryButton(
                            onClick = {
                                val geoUri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(Vehiculo)")
                                val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                val webUri = Uri.parse("https://maps.google.com/?q=${location.latitude},${location.longitude}")
                                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                try {
                                    context.startActivity(mapIntent)
                                } catch (_: Exception) {
                                    try {
                                        context.startActivity(webIntent)
                                    } catch (_: Exception) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = ClipData.newPlainText("Google Maps", webUri.toString())
                                        clipboard?.setPrimaryClip(clip)
                                        Toast.makeText(context, context.getString(R.string.detail_map_not_found), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            label = stringResource(R.string.detail_open_map),
                        )
                    }
                }
                item {
                    val now = System.currentTimeMillis()
                    if (location.expiresAt != null) {
                        val remaining = location.expiresAt - now
                        val isExpired = remaining <= 0
                        val timerLabel = if (isExpired) {
                            val expiredMin = ((-remaining) / 60_000L).coerceAtLeast(0L)
                            stringResource(R.string.timer_expired_ago, expiredMin)
                        } else {
                            val remMin = (remaining / 60_000L).coerceAtLeast(0L)
                            if (remMin < 60) {
                                stringResource(R.string.timer_remaining_minutes, remMin)
                            } else {
                                stringResource(R.string.timer_remaining_hours_minutes, remMin / 60, remMin % 60)
                            }
                        }
                        val expireTimeStr = remember(location.expiresAt) {
                            TimeFormatter.formatExpireTime(location.expiresAt)
                        }

                        val totalDuration = (location.expiresAt - location.parkedAt).coerceAtLeast(1L)
                        val timerProgress = ((location.expiresAt - now).toFloat() / totalDuration).coerceIn(0f, 1f)
                        val progressColor = if (isExpired || timerProgress <= 0.2f) colors.error else colors.primary

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isExpired) colors.errorContainer.copy(alpha = 0.35f) else colors.surfaceContainer)
                                .padding(spacing.SpaceMd),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.SpaceXs),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Canvas(modifier = Modifier.size(18.dp)) {
                                    drawCircle(
                                        color = colors.surfaceContainerHigh,
                                        style = Stroke(width = 2.5.dp.toPx()),
                                    )
                                    drawArc(
                                        color = progressColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f * timerProgress,
                                        useCenter = false,
                                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                                    )
                                }
                                Text(
                                    text = if (isExpired) stringResource(R.string.timer_expired) else stringResource(R.string.timer_title),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = progressColor,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Text(
                                text = timerLabel,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isExpired) colors.error else colors.onSurface,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = stringResource(R.string.timer_expires_at, expireTimeStr),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                item {
                    ParkSecondaryButton(
                        onClick = onSetTimerClick,
                        label = if (location.expiresAt != null) {
                            stringResource(R.string.timer_edit_button)
                        } else {
                            stringResource(R.string.timer_set_button)
                        },
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    ParkSecondaryButton(
                        onClick = onEditClick,
                        label = stringResource(R.string.detail_edit),
                    )
                }
                item {
                    ParkSecondaryButton(
                        onClick = {
                            val shareText = buildString {
                                append(context.getString(R.string.share_location_title))
                                append("\n${context.getString(R.string.save_parking_floor_label)}: ${location.floor}")
                                append("\n${context.getString(R.string.save_parking_zone_label)}: ${location.zone}")
                                append("\n${context.getString(R.string.save_parking_row_label)}: ${location.row}")
                                if (!location.spot.isNullOrBlank()) {
                                    append("\n${context.getString(R.string.save_parking_spot_label)}: ${location.spot}")
                                }
                                if (location.latitude != null && location.longitude != null) {
                                    append("\nhttps://maps.google.com/?q=${location.latitude},${location.longitude}")
                                }
                            }

                            // 1. Guaranteed action on Wear OS: copy to clipboard and notify with Toast
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("ParkWatch", shareText)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, context.getString(R.string.detail_share_copied), Toast.LENGTH_SHORT).show()

                            // 2. Also try system chooser in case messaging apps (e.g. WhatsApp) are installed
                            try {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                val chooser = Intent.createChooser(sendIntent, context.getString(R.string.detail_share)).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(chooser)
                            } catch (_: ActivityNotFoundException) {
                            }
                        },
                        label = stringResource(R.string.detail_share),
                    )
                }
                item {
                    ParkDestructiveButton(
                        onClick = { isConfirmingDelete.value = true },
                        label = stringResource(R.string.detail_delete),
                    )
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.delete_confirm_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.delete_confirm_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    ParkDestructiveButton(
                        onClick = onDeleteConfirmed,
                        label = stringResource(R.string.delete_confirm_delete),
                    )
                }
                item {
                    ParkSecondaryButton(
                        onClick = { isConfirmingDelete.value = false },
                        label = stringResource(R.string.delete_confirm_cancel),
                    )
                }
            }
        }
    }
}
