package com.david.parkwatch.presentation.save

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.RadioButton
import androidx.wear.compose.material3.RadioButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

private enum class SaveStep {
    FLOOR,
    ZONE,
    ROW,
}

@Composable
fun SaveParkingScreen(
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val floors = stringArrayResource(R.array.save_parking_floor_options).toList()
    val zones = stringArrayResource(R.array.save_parking_zone_options).toList()
    val rows = stringArrayResource(R.array.save_parking_row_options).toList()

    val currentStep = remember { mutableStateOf(SaveStep.FLOOR) }
    val selectedFloor = remember { mutableStateOf<String?>(null) }
    val selectedZone = remember { mutableStateOf<String?>(null) }
    val selectedRow = remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = currentStep.value != SaveStep.FLOOR) {
        currentStep.value = when (currentStep.value) {
            SaveStep.ROW -> SaveStep.ZONE
            SaveStep.ZONE -> SaveStep.FLOOR
            SaveStep.FLOOR -> SaveStep.FLOOR
        }
    }

    val options = when (currentStep.value) {
        SaveStep.FLOOR -> floors
        SaveStep.ZONE -> zones
        SaveStep.ROW -> rows
    }

    val promptText = stringResource(
        when (currentStep.value) {
            SaveStep.FLOOR -> R.string.save_parking_floor_prompt
            SaveStep.ZONE -> R.string.save_parking_zone_prompt
            SaveStep.ROW -> R.string.save_parking_row_prompt
        }
    )

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
                vertical = spacing.SpaceLg,
                horizontal = spacing.SpaceMd,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.save_parking_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                    Text(
                        text = promptText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }
            items(options) { option ->
                RadioButton(
                    selected = when (currentStep.value) {
                        SaveStep.FLOOR -> selectedFloor.value == option
                        SaveStep.ZONE -> selectedZone.value == option
                        SaveStep.ROW -> selectedRow.value == option
                    },
                    onSelect = {
                        when (currentStep.value) {
                            SaveStep.FLOOR -> selectedFloor.value = option
                            SaveStep.ZONE -> selectedZone.value = option
                            SaveStep.ROW -> selectedRow.value = option
                        }
                    },
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
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                )
            }
            item {
                ParkPrimaryButton(
                    onClick = {
                        when (currentStep.value) {
                            SaveStep.FLOOR -> currentStep.value = SaveStep.ZONE
                            SaveStep.ZONE -> currentStep.value = SaveStep.ROW
                            SaveStep.ROW -> {
                                // TODO: Siguiente bloque (puesto opcional / revisión)
                            }
                        }
                    },
                    label = stringResource(R.string.save_parking_continue),
                    enabled = when (currentStep.value) {
                        SaveStep.FLOOR -> selectedFloor.value != null
                        SaveStep.ZONE -> selectedZone.value != null
                        SaveStep.ROW -> selectedRow.value != null
                    },
                )
            }
        }
    }
}
