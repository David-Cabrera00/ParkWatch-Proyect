package com.david.parkwatch.presentation.save

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.RadioButton
import androidx.wear.compose.material3.RadioButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.data.location.LocationHelper
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import com.david.parkwatch.domain.parser.ParsedParkingInfo
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.component.parkRotaryScroll
import com.david.parkwatch.presentation.component.ParkLocationCard
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.component.VehicleTypeSelector
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing
import java.util.UUID

private enum class SaveStep {
    FLOOR,
    ZONE,
    ROW,
    SPOT,
    REVIEW,
    CONFIRMATION,
}

@Composable
fun SaveParkingScreen(
    modifier: Modifier = Modifier,
    initialLocation: ParkingLocation? = null,
    initialParsed: ParsedParkingInfo? = null,
    onSaveSuccess: (ParkingLocation) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val floors = stringArrayResource(R.array.save_parking_floor_options).toList()
    val zones = stringArrayResource(R.array.save_parking_zone_options).toList()
    val rows = stringArrayResource(R.array.save_parking_row_options).toList()
    val spots = stringArrayResource(R.array.save_parking_spot_options).toList()
    val noneSpotLabel = stringResource(R.string.save_parking_spot_none)

    val currentStep = remember {
        mutableStateOf(
            when {
                initialParsed?.isComplete == true -> SaveStep.REVIEW
                initialParsed?.floor != null && initialParsed.zone != null -> SaveStep.ROW
                initialParsed?.floor != null -> SaveStep.ZONE
                else -> SaveStep.FLOOR
            }
        )
    }
    val selectedFloor = remember { mutableStateOf<String?>(initialLocation?.floor ?: initialParsed?.floor) }
    val selectedZone = remember { mutableStateOf<String?>(initialLocation?.zone ?: initialParsed?.zone) }
    val selectedRow = remember { mutableStateOf<String?>(initialLocation?.row ?: initialParsed?.row) }
    val selectedSpot = remember { mutableStateOf<String?>(initialLocation?.spot ?: initialParsed?.spot) }
    val selectedVehicleType = remember {
        mutableStateOf(initialLocation?.vehicleType ?: initialParsed?.vehicleType ?: VehicleType.CAR)
    }
    val haptic = LocalHapticFeedback.current
    val listState = rememberScalingLazyListState()

    LaunchedEffect(currentStep.value) {
        listState.scrollToItem(0)
    }

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val capturedCoordinates = remember {
        mutableStateOf(
            if (initialLocation?.latitude != null && initialLocation.longitude != null) {
                Pair(initialLocation.latitude, initialLocation.longitude)
            } else {
                null
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            val coords = locationHelper.getLastKnownCoordinates()
            if (coords != null) {
                capturedCoordinates.value = coords
            }
        }
    }

    LaunchedEffect(Unit) {
        if (initialLocation == null && capturedCoordinates.value == null) {
            if (locationHelper.hasLocationPermission()) {
                val coords = locationHelper.getLastKnownCoordinates()
                if (coords != null) {
                    capturedCoordinates.value = coords
                }
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )
            }
        }
    }

    BackHandler(enabled = currentStep.value != SaveStep.FLOOR) {
        currentStep.value = when (currentStep.value) {
            SaveStep.FLOOR -> SaveStep.FLOOR
            SaveStep.ZONE -> SaveStep.FLOOR
            SaveStep.ROW -> SaveStep.ZONE
            SaveStep.SPOT -> SaveStep.ROW
            SaveStep.REVIEW -> SaveStep.SPOT
            SaveStep.CONFIRMATION -> {
                onNavigateBack()
                SaveStep.CONFIRMATION
            }
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
                vertical = spacing.SpaceLg,
                horizontal = spacing.SpaceMd,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (currentStep.value) {
                SaveStep.FLOOR, SaveStep.ZONE, SaveStep.ROW -> {
                    val options = when (currentStep.value) {
                        SaveStep.FLOOR -> floors
                        SaveStep.ZONE -> zones
                        else -> rows
                    }
                    val promptRes = when (currentStep.value) {
                        SaveStep.FLOOR -> R.string.save_parking_floor_prompt
                        SaveStep.ZONE -> R.string.save_parking_zone_prompt
                        else -> R.string.save_parking_row_prompt
                    }

                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.save_parking_step, currentStep.value.ordinal + 1),
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceXs))
                            Text(
                                text = stringResource(R.string.save_parking_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onBackground,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceXs))
                            Text(
                                text = stringResource(promptRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                        }
                    }

                    if (currentStep.value == SaveStep.ZONE) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(spacing.SpaceSm)) {
                                zones.forEach { option ->
                                    Text(
                                        text = option,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(if (selectedZone.value == option) colors.primaryContainer else colors.surfaceContainer)
                                            .selectable(
                                                selected = selectedZone.value == option,
                                                onClick = {
                                                    selectedZone.value = option
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                },
                                                role = Role.RadioButton,
                                            )
                                            .wrapContentSize(Alignment.Center),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (selectedZone.value == option) colors.onPrimaryContainer else colors.onSurface,
                                    )
                                }
                            }
                        }
                    } else {
                    items(options) { option ->
                        val isSelected = when (currentStep.value) {
                            SaveStep.FLOOR -> selectedFloor.value == option
                            SaveStep.ZONE -> selectedZone.value == option
                            else -> selectedRow.value == option
                        }
                        RadioButton(
                            selected = isSelected,
                            onSelect = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                when (currentStep.value) {
                                    SaveStep.FLOOR -> selectedFloor.value = option
                                    SaveStep.ZONE -> selectedZone.value = option
                                    else -> selectedRow.value = option
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
                    }

                    item {
                        val isEnabled = when (currentStep.value) {
                            SaveStep.FLOOR -> selectedFloor.value != null
                            SaveStep.ZONE -> selectedZone.value != null
                            else -> selectedRow.value != null
                        }
                        ParkPrimaryButton(
                            onClick = {
                                when (currentStep.value) {
                                    SaveStep.FLOOR -> currentStep.value = SaveStep.ZONE
                                    SaveStep.ZONE -> currentStep.value = SaveStep.ROW
                                    else -> currentStep.value = SaveStep.SPOT
                                }
                            },
                            label = stringResource(R.string.save_parking_continue),
                            enabled = isEnabled,
                        )
                    }
                }

                SaveStep.SPOT -> {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.save_parking_step, currentStep.value.ordinal + 1),
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceXs))
                            Text(
                                text = stringResource(R.string.save_parking_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onBackground,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceXs))
                            Text(
                                text = stringResource(R.string.save_parking_spot_prompt),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                        }
                    }

                    item {
                        RadioButton(
                            selected = selectedSpot.value == null,
                            onSelect = {
                                selectedSpot.value = null
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                    text = noneSpotLabel,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            },
                        )
                    }

                    items(spots) { spot ->
                        RadioButton(
                            selected = selectedSpot.value == spot,
                            onSelect = {
                                selectedSpot.value = spot
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                    text = spot,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            },
                        )
                    }

                    item {
                        val buttonLabel = if (selectedSpot.value != null) {
                            stringResource(R.string.save_parking_continue)
                        } else {
                            stringResource(R.string.save_parking_spot_skip)
                        }
                        ParkPrimaryButton(
                            onClick = {
                                currentStep.value = SaveStep.REVIEW
                            },
                            label = buttonLabel,
                            enabled = true,
                        )
                    }
                }

                SaveStep.REVIEW -> {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.save_parking_review_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onBackground,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceSm))
                            ParkLocationCard(
                                floor = selectedFloor.value.orEmpty(),
                                zone = selectedZone.value.orEmpty(),
                                row = selectedRow.value.orEmpty(),
                                spot = selectedSpot.value ?: noneSpotLabel,
                                vehicleType = selectedVehicleType.value,
                            )
                            if (capturedCoordinates.value != null) {
                                Spacer(modifier = Modifier.size(spacing.SpaceXs))
                                Text(
                                    text = stringResource(R.string.save_parking_gps_captured),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.secondary,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    item {
                        VehicleTypeSelector(
                            selectedVehicle = selectedVehicleType.value,
                            onVehicleSelected = { selectedVehicleType.value = it },
                        )
                    }

                    item {
                        ParkPrimaryButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val location = ParkingLocation(
                                    id = initialLocation?.id ?: UUID.randomUUID().toString(),
                                    floor = selectedFloor.value.orEmpty(),
                                    zone = selectedZone.value.orEmpty(),
                                    row = selectedRow.value.orEmpty(),
                                    spot = selectedSpot.value,
                                    latitude = capturedCoordinates.value?.first,
                                    longitude = capturedCoordinates.value?.second,
                                    parkedAt = initialLocation?.parkedAt ?: System.currentTimeMillis(),
                                    vehicleType = selectedVehicleType.value,
                                    createdAt = initialLocation?.createdAt ?: System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis(),
                                )
                                onSaveSuccess(location)
                                currentStep.value = SaveStep.CONFIRMATION
                            },
                            label = stringResource(R.string.save_parking_save),
                        )
                    }

                    item {
                        ParkSecondaryButton(
                            onClick = {
                                currentStep.value = SaveStep.FLOOR
                            },
                            label = stringResource(R.string.save_parking_edit),
                        )
                    }
                }

                SaveStep.CONFIRMATION -> {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.save_parking_success_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.primary,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceXs))
                            Text(
                                text = stringResource(R.string.save_parking_success_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceMd))
                            ParkLocationCard(
                                floor = selectedFloor.value.orEmpty(),
                                zone = selectedZone.value.orEmpty(),
                                row = selectedRow.value.orEmpty(),
                                spot = selectedSpot.value ?: noneSpotLabel,
                                vehicleType = selectedVehicleType.value,
                            )
                            if (capturedCoordinates.value != null) {
                                Spacer(modifier = Modifier.size(spacing.SpaceXs))
                                Text(
                                    text = stringResource(R.string.save_parking_gps_captured),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.secondary,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.size(spacing.SpaceSm))
                    }

                    item {
                        ParkPrimaryButton(
                            onClick = onNavigateBack,
                            label = stringResource(R.string.save_parking_done),
                        )
                    }
                }
            }
        }
    }
}
