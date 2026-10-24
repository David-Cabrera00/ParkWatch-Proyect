package com.david.parkwatch.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Shapes
import androidx.wear.compose.material3.Typography

@Composable
fun ParkWatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = remember(darkTheme) { ParkWatchColorScheme(darkTheme) }
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

private fun ParkWatchColorScheme(darkTheme: Boolean): ColorScheme {
    val palette = if (darkTheme) DarkPalette else LightPalette
    return ColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        secondary = palette.secondary,
        onSecondary = palette.onSecondary,
        tertiary = palette.secondary,
        onTertiary = palette.onSecondary,
        tertiaryContainer = palette.primaryContainer,
        onTertiaryContainer = palette.onPrimaryContainer,
        error = palette.error,
        onError = palette.onPrimary,
        errorContainer = palette.primaryContainer,
        onErrorContainer = palette.onPrimaryContainer,
        background = palette.background,
        onBackground = palette.textPrimary,
        onSurface = palette.textPrimary,
        onSurfaceVariant = palette.textSecondary,
        outline = palette.outline,
        outlineVariant = palette.outline,
        surfaceContainerLow = palette.surface,
        surfaceContainer = palette.surface,
        surfaceContainerHigh = palette.surfaceHigh,
    )
}

private data class ParkPalette(
    val background: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val outline: Color,
    val error: Color,
)

private val DarkPalette = ParkPalette(
    background = ParkColors.ParkBackground,
    surface = ParkColors.ParkSurface,
    surfaceHigh = ParkColors.ParkSurfaceHigh,
    primary = ParkColors.ParkPrimary,
    onPrimary = ParkColors.ParkOnPrimary,
    primaryContainer = ParkColors.ParkPrimaryContainer,
    onPrimaryContainer = ParkColors.ParkOnPrimaryContainer,
    secondary = ParkColors.ParkSecondary,
    onSecondary = ParkColors.ParkOnSecondary,
    textPrimary = ParkColors.ParkTextPrimary,
    textSecondary = ParkColors.ParkTextSecondary,
    outline = ParkColors.ParkOutline,
    error = ParkColors.ParkError,
)

private val LightPalette = ParkPalette(
    background = Color(0xFFE2E6EA),
    surface = Color(0xFFEEF2F5),
    surfaceHigh = Color(0xFFD2D8DF),
    primary = Color(0xFF166534),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC0ECCB),
    onPrimaryContainer = Color(0xFF0F3B1F),
    secondary = Color(0xFF1E5B84),
    onSecondary = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF111827),
    textSecondary = Color(0xFF4B5563),
    outline = Color(0xFF9AA2AC),
    error = Color(0xFFBA1A1A),
)

@androidx.compose.runtime.Composable
fun parkColors(): ColorScheme = MaterialTheme.colorScheme
