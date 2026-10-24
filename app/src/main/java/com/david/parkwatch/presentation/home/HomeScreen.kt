package com.david.parkwatch.presentation.home

import androidx.compose.foundation.background
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
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
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
                item {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(50))
                            .background(colors.surfaceContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_directions_car),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = colors.primary,
                        )
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.home_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                }
                item {
                    ParkPrimaryButton(
                        onClick = onSaveVehicleClick,
                        label = stringResource(R.string.home_save_vehicle),
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Button(
                            onClick = onQuickSaveClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = colors.surfaceContainerHigh,
                                contentColor = colors.onSurface,
                            ),
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_location_on),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = colors.primary,
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(
                                        text = stringResource(R.string.home_quick_save_chip),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                    )
                                }
                            },
                        )
                        Button(
                            onClick = onVoiceClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = colors.surfaceContainerHigh,
                                contentColor = colors.onSurface,
                            ),
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_mic),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = colors.primary,
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(
                                        text = stringResource(R.string.home_voice_chip),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                    )
                                }
                            },
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Button(
                            onClick = onHistoryClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = colors.surfaceContainer,
                                contentColor = colors.onSurfaceVariant,
                            ),
                            label = {
                                Text(
                                    text = stringResource(R.string.history_title),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            },
                        )
                        Button(
                            onClick = onSettingsClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = colors.surfaceContainer,
                                contentColor = colors.onSurfaceVariant,
                            ),
                            label = {
                                Text(
                                    text = stringResource(R.string.settings_open),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            },
                        )
                    }
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
                        elapsedTime = TimeFormatter.formatElapsedTime(location.parkedAt, context),
                        timerInfo = timerInfo,
                        isTimerExpired = isExpired,
                        onClick = onViewVehicleClick,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
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
