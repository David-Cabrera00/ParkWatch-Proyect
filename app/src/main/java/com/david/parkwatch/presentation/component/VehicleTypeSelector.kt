package com.david.parkwatch.presentation.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.david.parkwatch.domain.model.VehicleType
import com.david.parkwatch.presentation.model.iconRes
import com.david.parkwatch.presentation.model.labelRes
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun VehicleTypeSelector(
    selectedVehicle: VehicleType,
    onVehicleSelected: (VehicleType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VehicleType.entries.forEachIndexed { index, vehicle ->
                if (index > 0) {
                    Spacer(modifier = Modifier.size(6.dp))
                }
                val isSelected = vehicle == selectedVehicle
                val background = if (isSelected) colors.primaryContainer else colors.surfaceContainer
                val tint = if (isSelected) colors.onPrimaryContainer else colors.onSurfaceVariant
                val buttonSize by animateDpAsState(
                    targetValue = if (isSelected) 40.dp else 34.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "vehicleButtonSize",
                )
                val iconSize by animateDpAsState(
                    targetValue = if (isSelected) 22.dp else 17.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "vehicleIconSize",
                )
                val borderModifier = if (isSelected) {
                    Modifier.border(1.5.dp, colors.primary, CircleShape)
                } else Modifier

                Box(
                    modifier = Modifier
                        .size(buttonSize)
                        .clip(CircleShape)
                        .then(borderModifier)
                        .background(background)
                        .clickable {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onVehicleSelected(vehicle)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(vehicle.iconRes),
                        contentDescription = stringResource(vehicle.labelRes),
                        modifier = Modifier.size(iconSize),
                        tint = tint,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(spacing.SpaceXs))
        Text(
            text = stringResource(selectedVehicle.labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = colors.primary,
        )
    }
}

