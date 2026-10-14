package com.david.parkwatch.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.component.ParkLocationCard
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    location: ParkingLocation? = null,
    onSaveVehicleClick: () -> Unit,
    onVoiceClick: () -> Unit = {},
    onViewVehicleClick: () -> Unit = {},
) {
    val colors = parkColors()
    val spacing = parkSpacing()

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(
                top = spacing.SpaceMd,
                bottom = spacing.SpaceXl,
                start = spacing.SpaceMd,
                end = spacing.SpaceMd,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.SpaceXs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (location == null) {
                item {
                    Icon(
                        painter = painterResource(R.drawable.ic_directions_car),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = colors.primary,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    Text(
                        text = "ParkWatch",
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
                        onClick = onVoiceClick,
                        label = stringResource(R.string.home_voice_input),
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
                    val elapsedMinutes = ((System.currentTimeMillis() - location.parkedAt) / 60_000L)
                        .coerceAtLeast(0L)
                        .toInt()
                    ParkLocationCard(
                        floor = location.floor,
                        zone = location.zone,
                        row = location.row,
                        spot = location.spot,
                        elapsedTime = stringResource(R.string.detail_time_minutes, elapsedMinutes),
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
            }
        }
    }
}
