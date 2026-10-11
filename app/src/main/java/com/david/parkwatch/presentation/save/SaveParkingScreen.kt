package com.david.parkwatch.presentation.save

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.RadioButton
import androidx.wear.compose.material3.RadioButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun SaveParkingScreen(
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val floors = listOf("P1", "P2", "P3", "B1", "B2")
    val selectedFloor = remember { mutableStateOf<String?>(null) }

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(spacing.SpaceLg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.save_parking_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Spacer(modifier = Modifier.size(spacing.SpaceXs))
            Text(
                text = stringResource(R.string.save_parking_floor_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Spacer(modifier = Modifier.size(spacing.SpaceMd))
            floors.forEach { floor ->
                RadioButton(
                    selected = selectedFloor.value == floor,
                    onSelect = { selectedFloor.value = floor },
                    colors = RadioButtonDefaults.radioButtonColors(
                        selectedContainerColor = colors.primaryContainer,
                        selectedContentColor = colors.onPrimaryContainer,
                        selectedControlColor = colors.primary,
                        unselectedContainerColor = colors.surfaceContainer,
                        unselectedContentColor = colors.onSurface,
                        unselectedControlColor = colors.onSurfaceVariant,
                    ),
                    label = {
                        Text(
                            text = floor,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                )
                if (floor != floors.last()) {
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                }
            }
        }
    }
}
