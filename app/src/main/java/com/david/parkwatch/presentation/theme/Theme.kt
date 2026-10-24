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
import com.david.parkwatch.data.preferences.ParkAccentColor

@Composable
fun ParkWatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: ParkAccentColor = ParkAccentColor.EMERALD,
    content: @Composable () -> Unit
) {
    val colorScheme = remember(darkTheme, accentColor) { ParkWatchColorScheme(darkTheme, accentColor) }
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

private data class AccentColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
)

private fun getAccentColors(accentColor: ParkAccentColor, darkTheme: Boolean): AccentColors {
    return when (accentColor) {
        ParkAccentColor.EMERALD -> if (darkTheme) {
            AccentColors(ParkColors.ParkPrimary, ParkColors.ParkOnPrimary, ParkColors.ParkPrimaryContainer, ParkColors.ParkOnPrimaryContainer)
        } else {
            AccentColors(Color(0xFF166534), Color.White, Color(0xFFC0ECCB), Color(0xFF0F3B1F))
        }
        ParkAccentColor.CYAN -> if (darkTheme) {
            AccentColors(Color(0xFF5CE1E6), Color(0xFF002022), Color(0xFF00373A), Color(0xFFA6F5F8))
        } else {
            AccentColors(Color(0xFF0E7490), Color.White, Color(0xFFBAE6FD), Color(0xFF082F49))
        }
        ParkAccentColor.AMBER -> if (darkTheme) {
            AccentColors(Color(0xFFFFB74D), Color(0xFF261800), Color(0xFF4A3000), Color(0xFFFFE0B2))
        } else {
            AccentColors(Color(0xFFB45309), Color.White, Color(0xFFFED7AA), Color(0xFF451A03))
        }
        ParkAccentColor.RUBY -> if (darkTheme) {
            AccentColors(Color(0xFFFF6565), Color(0xFF2B0000), Color(0xFF520B0B), Color(0xFFFFCDCD))
        } else {
            AccentColors(Color(0xFFB91C1C), Color.White, Color(0xFFFECACA), Color(0xFF450A0A))
        }
    }
}

private fun ParkWatchColorScheme(darkTheme: Boolean, accentColor: ParkAccentColor): ColorScheme {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val accents = getAccentColors(accentColor, darkTheme)

    return ColorScheme(
        primary = accents.primary,
        onPrimary = accents.onPrimary,
        primaryContainer = accents.primaryContainer,
        onPrimaryContainer = accents.onPrimaryContainer,
        secondary = palette.secondary,
        onSecondary = palette.onSecondary,
        tertiary = palette.secondary,
        onTertiary = palette.onSecondary,
        tertiaryContainer = accents.primaryContainer,
        onTertiaryContainer = accents.onPrimaryContainer,
        error = palette.error,
        onError = accents.onPrimary,
        errorContainer = accents.primaryContainer,
        onErrorContainer = accents.onPrimaryContainer,
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
