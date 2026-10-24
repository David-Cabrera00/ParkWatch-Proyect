package com.david.parkwatch.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.component.ParkLocationCard
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.component.parkRotaryScroll
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    location: ParkingLocation? = null,
    onSaveVehicleClick: () -> Unit,
    onQuickSaveClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onViewVehicleClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val listState = rememberScalingLazyListState()

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
                item {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(50))
                            .background(colors.surfaceContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_directions_car),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = colors.primary,
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    Text(
                        text = stringResource(R.string.home_brand),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                }
                item {
                    Text(
                        text = stringResource(R.string.home_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.home_empty_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceMd))
                }
                item {
                    ParkPrimaryButton(
                        onClick = onSaveVehicleClick,
                        label = stringResource(R.string.home_save_vehicle),
                    )
                }
                item {
                    ParkSecondaryButton(
                        onClick = onQuickSaveClick,
                        label = stringResource(R.string.home_quick_save),
                    )
                }
                item {
                    ParkSecondaryButton(
                        onClick = onVoiceClick,
                        label = stringResource(R.string.home_voice_input),
                    )
                }
                item {
                    ParkSecondaryButton(
                        onClick = onSettingsClick,
                        label = stringResource(R.string.settings_open),
                    )
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.home_saved_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    val now = System.currentTimeMillis()
                    val elapsedMinutes = ((now - location.parkedAt) / 60_000L)
                        .coerceAtLeast(0L)
                        .toInt()
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

                    ParkLocationCard(
                        floor = location.floor,
                        zone = location.zone,
                        row = location.row,
                        spot = location.spot,
                        vehicleType = location.vehicleType,
                        elapsedTime = stringResource(R.string.detail_time_minutes, elapsedMinutes),
                        timerInfo = timerInfo,
                        isTimerExpired = isExpired,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    ParkPrimaryButton(
                        onClick = onViewVehicleClick,
                        label = stringResource(R.string.home_view_vehicle),
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
