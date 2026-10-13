package com.david.parkwatch.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    location: ParkingLocation? = null,
    onSaveVehicleClick: () -> Unit,
    onViewVehicleClick: () -> Unit = {},
) {
    val colors = parkColors()
    val spacing = parkSpacing()

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            if (location == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = spacing.SpaceLg),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "ParkWatch",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(spacing.SpaceMd))
                    Text(
                        text = stringResource(R.string.home_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                    Text(
                        text = stringResource(R.string.home_empty_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(spacing.SpaceXl))
                    ParkPrimaryButton(
                        onClick = onSaveVehicleClick,
                        label = stringResource(R.string.home_save_vehicle),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = spacing.SpaceLg),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.home_saved_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                    Text(
                        text = location.floor,
                        style = MaterialTheme.typography.numeralLarge,
                        color = colors.primary,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = buildString {
                            append("Zona ${location.zone} • Fila ${location.row}")
                            location.spot?.let { append(" • Puesto $it") }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(spacing.SpaceLg))
                    ParkPrimaryButton(
                        onClick = onViewVehicleClick,
                        label = stringResource(R.string.home_view_vehicle),
                    )
                }
            }
        }
    }
}
