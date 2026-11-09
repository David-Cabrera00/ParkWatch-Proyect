package com.david.parkwatch.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import com.david.parkwatch.presentation.theme.parkColors

/**
 * Standard Wear OS icon button following ParkWatch Design System (MASTER.md §11).
 * Guarantees a minimum touch target of 48x48 dp for wrist ergonomics.
 */
@Composable
fun ParkIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 22.dp,
    containerColor: Color? = null,
    iconTint: Color? = null,
    borderColor: Color? = null,
    enabled: Boolean = true,
) {
    val colors = parkColors()
    val haptic = LocalHapticFeedback.current

    val effectiveContainerColor = containerColor ?: colors.surfaceContainerHigh
    val effectiveIconTint = iconTint ?: colors.primary
    val borderModifier = if (borderColor != null) {
        Modifier.border(1.dp, borderColor, CircleShape)
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(borderModifier)
            .background(if (enabled) effectiveContainerColor else effectiveContainerColor.copy(alpha = 0.38f))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            )
            .semantics {
                this.role = Role.Button
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = if (enabled) effectiveIconTint else effectiveIconTint.copy(alpha = 0.38f),
        )
    }
}
