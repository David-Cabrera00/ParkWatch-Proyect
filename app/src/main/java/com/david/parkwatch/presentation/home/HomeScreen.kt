package com.david.parkwatch.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.component.ParkIconButton
import com.david.parkwatch.presentation.component.ParkLocationCard
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.component.parkRotaryScroll
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing
import com.david.parkwatch.presentation.util.TimeFormatter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    location: ParkingLocation? = null,
    onSaveVehicleClick: () -> Unit,
    onQuickSaveClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onViewVehicleClick: () -> Unit = {},
    onCompassClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val listState = rememberScalingLazyListState()
    val context = LocalContext.current

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
            if (location == null) {
                // MASTER.md §30.1: simple vehicle icon inside a tonal circular container with ambient glow
                item {
                    Box(contentAlignment = Alignment.Center) {
                        // Ambient radial glow
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .background(
                                    androidx.compose.ui.graphics.Brush.radialGradient(
                                        colors = listOf(
                                            colors.primary.copy(alpha = 0.22f),
                                            androidx.compose.ui.graphics.Color.Transparent,
                                        ),
                                    ),
                                    CircleShape,
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceContainer)
                                .border(
                                    BorderStroke(1.dp, colors.primary.copy(alpha = 0.4f)),
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_directions_car),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = colors.primary,
                            )
                        }
                    }
                }
                // Short message: Where did you park?
                item {
                    Text(
                        text = stringResource(R.string.home_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
                // Brief supporting line
                item {
                    Text(
                        text = stringResource(R.string.home_empty_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                }
                // Dominant primary action: Save vehicle
                item {
                    ParkPrimaryButton(
                        onClick = onSaveVehicleClick,
                        label = stringResource(R.string.home_save_vehicle),
                    )
                }
                // Ergonomic wrist actions row: Voice input, Quick GPS, History, Settings
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        ParkIconButton(
                            iconRes = R.drawable.ic_mic,
                            contentDescription = stringResource(R.string.home_voice_input),
                            onClick = onVoiceClick,
                            size = 44.dp,
                            iconSize = 20.dp,
                            containerColor = colors.surfaceContainerHigh,
                            iconTint = colors.primary,
                        )
                        ParkIconButton(
                            iconRes = R.drawable.ic_location_on,
                            contentDescription = stringResource(R.string.home_quick_save),
                            onClick = onQuickSaveClick,
                            size = 44.dp,
                            iconSize = 20.dp,
                            containerColor = colors.surfaceContainerHigh,
                            iconTint = colors.secondary,
                        )
                        ParkIconButton(
                            iconRes = R.drawable.ic_history,
                            contentDescription = stringResource(R.string.history_title),
                            onClick = onHistoryClick,
                            size = 44.dp,
                            iconSize = 20.dp,
                            containerColor = colors.surfaceContainer,
                            iconTint = colors.onSurfaceVariant,
                        )
                        ParkIconButton(
                            iconRes = R.drawable.ic_settings,
                            contentDescription = stringResource(R.string.settings_open),
                            onClick = onSettingsClick,
                            size = 44.dp,
                            iconSize = 20.dp,
                            containerColor = colors.surfaceContainer,
                            iconTint = colors.onSurfaceVariant,
                        )
                    }
                }
            } else {
                // Saved state: Status header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.primary),
                        )
                        Text(
                            text = stringResource(R.string.home_saved_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                // Hero ParkLocationCard
                item {
                    val now = System.currentTimeMillis()
                    val timerInfo = if (location.expiresAt != null) {
                        val remaining = location.expiresAt - now
                        if (remaining <= 0) {
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
                    } else null

                    val isExpired = location.expiresAt != null && now >= location.expiresAt
                    val timerProgress = if (location.expiresAt != null) {
                        val totalDuration = (location.expiresAt - location.parkedAt).coerceAtLeast(1L)
                        ((location.expiresAt - now).toFloat() / totalDuration).coerceIn(0f, 1f)
                    } else null

                    ParkLocationCard(
                        floor = location.floor,
                        zone = location.zone,
                        row = location.row,
                        spot = location.spot,
                        vehicleType = location.vehicleType,
                        elapsedTime = TimeFormatter.formatElapsedTime(location.parkedAt, context),
                        timerInfo = timerInfo,
                        timerProgress = timerProgress,
                        isTimerExpired = isExpired,
                        onClick = onViewVehicleClick,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                }
                // Actions hierarchy: one primary CTA, then secondary
                if (location.latitude != null && location.longitude != null) {
                    item {
                        ParkPrimaryButton(
                            onClick = onCompassClick,
                            label = stringResource(R.string.compass_guide_me),
                        )
                    }
                    item {
                        ParkSecondaryButton(
                            onClick = onViewVehicleClick,
                            label = stringResource(R.string.home_view_vehicle),
                        )
                    }
                } else {
                    item {
                        ParkPrimaryButton(
                            onClick = onViewVehicleClick,
                            label = stringResource(R.string.home_view_vehicle),
                        )
                    }
                }
                item {
                    ParkSecondaryButton(
                        onClick = onHistoryClick,
                        label = stringResource(R.string.history_title),
                    )
                }
                item {
                    ParkSecondaryButton(
                        onClick = onSettingsClick,
                        label = stringResource(R.string.settings_open),
                    )
                }
            }
        }
    }
}
