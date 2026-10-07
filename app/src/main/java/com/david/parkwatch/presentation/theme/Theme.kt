package com.david.parkwatch.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Shapes
import androidx.wear.compose.material3.Typography

@Composable
fun ParkWatchTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = remember { ParkWatchColorScheme() }
    val typography = Typography()
    val shapes = Shapes()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
    ) {
        CompositionLocalProvider(
            LocalParkSpacing provides ParkSpacingImpl(),
        ) {
            content()
        }
    }
}

private fun ParkWatchColorScheme(): ColorScheme {
    return ColorScheme(
        primary = ParkColors.ParkPrimary,
        onPrimary = ParkColors.ParkOnPrimary,
        primaryContainer = ParkColors.ParkPrimaryContainer,
        onPrimaryContainer = ParkColors.ParkOnPrimaryContainer,
        secondary = ParkColors.ParkSecondary,
        onSecondary = ParkColors.ParkOnSecondary,
        tertiary = ParkColors.ParkSecondary,
        onTertiary = ParkColors.ParkOnSecondary,
        tertiaryContainer = ParkColors.ParkPrimaryContainer,
        onTertiaryContainer = ParkColors.ParkOnPrimaryContainer,
        error = ParkColors.ParkError,
        onError = ParkColors.ParkOnPrimary,
        errorContainer = ParkColors.ParkPrimaryContainer,
        onErrorContainer = ParkColors.ParkOnPrimaryContainer,
        background = ParkColors.ParkBackground,
        onBackground = ParkColors.ParkTextPrimary,
        onSurface = ParkColors.ParkTextPrimary,
        onSurfaceVariant = ParkColors.ParkTextSecondary,
        outline = ParkColors.ParkOutline,
        outlineVariant = ParkColors.ParkOutline,
        surfaceContainerLow = ParkColors.ParkSurface,
        surfaceContainer = ParkColors.ParkSurface,
        surfaceContainerHigh = ParkColors.ParkSurfaceHigh,
    )
}

@androidx.compose.runtime.Composable
fun parkColors(): ColorScheme = MaterialTheme.colorScheme