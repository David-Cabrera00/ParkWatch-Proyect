package com.david.parkwatch.presentation.save

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
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
import com.david.parkwatch.presentation.util.ParkHaptics
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

    val isEditMode = initialLocation != null
    var returnToReview by remember { mutableStateOf(false) }

    val currentStep = remember {
        mutableStateOf(
            when {
                initialLocation != null -> SaveStep.REVIEW
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

    BackHandler(enabled = true) {
        if (returnToReview) {
            returnToReview = false
            currentStep.value = SaveStep.REVIEW
        } else {
            when (currentStep.value) {
                SaveStep.FLOOR -> {
                    if (isEditMode) currentStep.value = SaveStep.REVIEW
                    else onNavigateBack()
                }
                SaveStep.ZONE -> currentStep.value = if (isEditMode) SaveStep.REVIEW else SaveStep.FLOOR
                SaveStep.ROW -> currentStep.value = if (isEditMode) SaveStep.REVIEW else SaveStep.ZONE
                SaveStep.SPOT -> currentStep.value = if (isEditMode) SaveStep.REVIEW else SaveStep.ROW
                SaveStep.REVIEW -> onNavigateBack()
                SaveStep.CONFIRMATION -> onNavigateBack()
            }
        }
    }

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        AnimatedContent(
            targetState = currentStep.value,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    (slideInHorizontally { width -> width / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> -width / 3 } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> width / 3 } + fadeOut())
                }
            },
            label = "saveStepTransition",
        ) { step ->
            val stepListState = rememberScalingLazyListState()
            ScalingLazyColumn(
                state = stepListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .parkRotaryScroll(stepListState),
                contentPadding = PaddingValues(
                    top = 28.dp,
                    bottom = 48.dp,
                    start = 14.dp,
                    end = 14.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (step) {
                SaveStep.FLOOR -> {
                    item {
                        StepHeader(
                            currentStepNumber = 1,
                            title = stringResource(R.string.save_parking_title),
                            prompt = stringResource(R.string.save_parking_floor_prompt),
                        )
                    }

                    items(floors) { option ->
                        val isSelected = selectedFloor.value == option
                        val borderModifier = if (isSelected) {
                            Modifier.border(1.5.dp, colors.primary, RoundedCornerShape(18.dp))
                        } else Modifier

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .then(borderModifier)
                                .background(if (isSelected) colors.primaryContainer else colors.surfaceContainer)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFloor.value = option
                                    if (returnToReview) {
                                        returnToReview = false
                                        currentStep.value = SaveStep.REVIEW
                                    } else {
                                        currentStep.value = SaveStep.ZONE
                                    }
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isSelected) colors.onPrimaryContainer else colors.onSurface,
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(colors.primary),
                                    )
                                }
                            }
                        }
                    }

                    if (selectedFloor.value != null) {
                        item {
                            ParkPrimaryButton(
                                onClick = {
                                    if (returnToReview) {
                                        returnToReview = false
                                        currentStep.value = SaveStep.REVIEW
                                    } else {
                                        currentStep.value = SaveStep.ZONE
                                    }
                                },
                                label = stringResource(R.string.save_parking_continue),
                            )
                        }
                    }
                }

                SaveStep.ZONE -> {
                    item {
                        StepHeader(
                            currentStepNumber = 2,
                            title = stringResource(R.string.save_parking_title),
                            prompt = stringResource(R.string.save_parking_zone_prompt),
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing.SpaceSm, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            zones.forEach { option ->
                                val isSelected = selectedZone.value == option
                                val borderModifier = if (isSelected) {
                                    Modifier.border(1.5.dp, colors.primary, RoundedCornerShape(50))
                                } else Modifier

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(50))
                                        .then(borderModifier)
                                        .background(if (isSelected) colors.primaryContainer else colors.surfaceContainer)
                                        .clickable {
                                            selectedZone.value = option
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            if (returnToReview) {
                                                returnToReview = false
                                                currentStep.value = SaveStep.REVIEW
                                            } else {
                                                currentStep.value = SaveStep.ROW
                                            }
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isSelected) colors.onPrimaryContainer else colors.onSurface,
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.size(spacing.SpaceXs))
                    }

                    item {
                        ParkSecondaryButton(
                            onClick = {
                                if (selectedRow.value == null) selectedRow.value = "—"
                                currentStep.value = SaveStep.REVIEW
                            },
                            label = stringResource(R.string.save_parking_jump_to_review),
                        )
                    }

                    if (selectedZone.value != null) {
                        item {
                            ParkPrimaryButton(
                                onClick = {
                                    if (returnToReview) {
                                        returnToReview = false
                                        currentStep.value = SaveStep.REVIEW
                                    } else {
                                        currentStep.value = SaveStep.ROW
                                    }
                                },
                                label = stringResource(R.string.save_parking_continue),
                            )
                        }
                    }
                }

                SaveStep.ROW -> {
                    item {
                        StepHeader(
                            currentStepNumber = 3,
                            title = stringResource(R.string.save_parking_title),
                            prompt = stringResource(R.string.save_parking_row_prompt),
                        )
                    }

                    item {
                        val isSelected = selectedRow.value == "—" || selectedRow.value == null
                        val borderModifier = if (isSelected) {
                            Modifier.border(1.5.dp, colors.primary, RoundedCornerShape(16.dp))
                        } else Modifier

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .then(borderModifier)
                                .background(if (isSelected) colors.primaryContainer else colors.surfaceContainer)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedRow.value = "—"
                                    returnToReview = false
                                    currentStep.value = SaveStep.REVIEW
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.save_parking_row_none),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) colors.onPrimaryContainer else colors.onSurface,
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(colors.primary),
                                    )
                                }
                            }
                        }
                    }

                    items(rows) { option ->
                        val isSelected = selectedRow.value == option
                        val borderModifier = if (isSelected) {
                            Modifier.border(1.5.dp, colors.primary, RoundedCornerShape(16.dp))
                        } else Modifier

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .then(borderModifier)
                                .background(if (isSelected) colors.primaryContainer else colors.surfaceContainer)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedRow.value = option
                                    if (returnToReview) {
                                        returnToReview = false
                                        currentStep.value = SaveStep.REVIEW
                                    } else {
                                        currentStep.value = SaveStep.SPOT
                                    }
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${stringResource(R.string.location_row)} $option",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) colors.onPrimaryContainer else colors.onSurface,
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(colors.primary),
                                    )
                                }
                            }
                        }
                    }

                    item {
                        ParkSecondaryButton(
                            onClick = {
                                if (selectedRow.value == null) selectedRow.value = "—"
                                returnToReview = false
                                currentStep.value = SaveStep.REVIEW
                            },
                            label = stringResource(R.string.save_parking_jump_to_review),
                        )
                    }

                    if (selectedRow.value != null && selectedRow.value != "—") {
                        item {
                            ParkPrimaryButton(
                                onClick = {
                                    if (returnToReview) {
                                        returnToReview = false
                                        currentStep.value = SaveStep.REVIEW
                                    } else {
                                        currentStep.value = SaveStep.SPOT
                                    }
                                },
                                label = stringResource(R.string.save_parking_continue),
                            )
                        }
                    }
                }

                SaveStep.SPOT -> {
                    item {
                        StepHeader(
                            currentStepNumber = 4,
                            title = stringResource(R.string.save_parking_title),
                            prompt = stringResource(R.string.save_parking_spot_prompt),
                        )
                    }

                    item {
                        val isSelected = selectedSpot.value == null
                        val borderModifier = if (isSelected) {
                            Modifier.border(1.5.dp, colors.primary, RoundedCornerShape(16.dp))
                        } else Modifier

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .then(borderModifier)
                                .background(if (isSelected) colors.primaryContainer else colors.surfaceContainer)
                                .clickable {
                                    selectedSpot.value = null
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentStep.value = SaveStep.REVIEW
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = noneSpotLabel,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) colors.onPrimaryContainer else colors.onSurface,
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(colors.primary),
                                    )
                                }
                            }
                        }
                    }

                    items(spots) { spot ->
                        val isSelected = selectedSpot.value == spot
                        val borderModifier = if (isSelected) {
                            Modifier.border(1.5.dp, colors.primary, RoundedCornerShape(16.dp))
                        } else Modifier

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .then(borderModifier)
                                .background(if (isSelected) colors.primaryContainer else colors.surfaceContainer)
                                .clickable {
                                    selectedSpot.value = spot
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentStep.value = SaveStep.REVIEW
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${stringResource(R.string.location_spot)} $spot",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) colors.onPrimaryContainer else colors.onSurface,
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(colors.primary),
                                    )
                                }
                            }
                        }
                    }

                    item {
                        ParkPrimaryButton(
                            onClick = {
                                currentStep.value = SaveStep.REVIEW
                            },
                            label = stringResource(R.string.save_parking_continue),
                        )
                    }
                }

                SaveStep.REVIEW -> {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(if (isEditMode) R.string.detail_edit else R.string.save_parking_review_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onBackground,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                            Spacer(modifier = Modifier.size(spacing.SpaceSm))
                            ParkLocationCard(
                                floor = selectedFloor.value.orEmpty(),
                                zone = selectedZone.value.orEmpty().ifEmpty { "—" },
                                row = selectedRow.value.orEmpty().ifEmpty { "—" },
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
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(spacing.SpaceXs, Alignment.CenterHorizontally),
                        ) {
                            Button(
                                onClick = {
                                    returnToReview = true
                                    currentStep.value = SaveStep.FLOOR
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.surfaceContainer,
                                    contentColor = colors.primary,
                                ),
                                modifier = Modifier.weight(1f).height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "${stringResource(R.string.save_parking_floor_label)}: ${selectedFloor.value ?: "—"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                )
                            }
                            Button(
                                onClick = {
                                    returnToReview = true
                                    currentStep.value = SaveStep.ZONE
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.surfaceContainer,
                                    contentColor = colors.primary,
                                ),
                                modifier = Modifier.weight(1f).height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "${stringResource(R.string.save_parking_zone_label)}: ${selectedZone.value?.takeIf { it.isNotEmpty() && it != "—" } ?: "—"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(spacing.SpaceXs, Alignment.CenterHorizontally),
                        ) {
                            Button(
                                onClick = {
                                    returnToReview = true
                                    currentStep.value = SaveStep.ROW
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.surfaceContainer,
                                    contentColor = colors.primary,
                                ),
                                modifier = Modifier.weight(1f).height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "${stringResource(R.string.save_parking_row_label)}: ${selectedRow.value?.takeIf { it.isNotEmpty() } ?: "—"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                )
                            }
                            Button(
                                onClick = {
                                    returnToReview = true
                                    currentStep.value = SaveStep.SPOT
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.surfaceContainer,
                                    contentColor = colors.primary,
                                ),
                                modifier = Modifier.weight(1f).height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "${stringResource(R.string.save_parking_spot_label)}: ${selectedSpot.value ?: "—"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
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
                                ParkHaptics.playSuccess(context)
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
                                    expiresAt = initialLocation?.expiresAt,
                                )
                                onSaveSuccess(location)
                                currentStep.value = SaveStep.CONFIRMATION
                            },
                            label = stringResource(R.string.save_parking_save),
                        )
                    }

                    if (!isEditMode) {
                        item {
                            ParkSecondaryButton(
                                onClick = {
                                    currentStep.value = SaveStep.FLOOR
                                },
                                label = stringResource(R.string.save_parking_edit),
                            )
                        }
                    }
                }

                SaveStep.CONFIRMATION -> {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(colors.primaryContainer)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                androidx.wear.compose.material3.Icon(
                                    painter = androidx.compose.ui.res.painterResource(R.drawable.ic_check),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = colors.primary,
                                )
                            }
                            Spacer(modifier = Modifier.size(spacing.SpaceSm))
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
                                zone = selectedZone.value.orEmpty().ifEmpty { "—" },
                                row = selectedRow.value.orEmpty().ifEmpty { "—" },
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
}

@Composable
private fun StepHeader(
    currentStepNumber: Int,
    totalSteps: Int = 4,
    title: String,
    prompt: String,
) {
    val colors = parkColors()
    val spacing = parkSpacing()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // MASTER.md §30.3: show Step X of 4 above the title
        Text(
            text = stringResource(R.string.save_parking_step, currentStepNumber),
            style = MaterialTheme.typography.labelSmall,
            color = colors.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.size(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (step in 1..totalSteps) {
                val isActive = step == currentStepNumber
                val isCompleted = step < currentStepNumber
                val pillWidth by animateDpAsState(
                    targetValue = if (isActive) 18.dp else 6.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "stepPillWidth",
                )
                val pillColor = if (isActive) colors.primary else if (isCompleted) colors.primary.copy(alpha = 0.5f) else colors.surfaceContainerHigh

                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(pillWidth)
                        .clip(RoundedCornerShape(50))
                        .background(pillColor),
                )
            }
        }
        Spacer(modifier = Modifier.size(spacing.SpaceXs))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
        Spacer(modifier = Modifier.size(spacing.SpaceXs))
        Text(
            text = prompt,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
