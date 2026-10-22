package com.david.parkwatch.presentation

import androidx.compose.runtime.Composable
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.david.parkwatch.data.preferences.AppPreferences
import com.david.parkwatch.data.preferences.ParkLanguage
import com.david.parkwatch.data.preferences.ParkThemeMode
import com.david.parkwatch.presentation.component.ParkLocationCard
import com.david.parkwatch.presentation.home.HomeScreen
import com.david.parkwatch.presentation.settings.SettingsScreen
import com.david.parkwatch.presentation.theme.ParkWatchTheme

@WearPreviewDevices
@Composable
fun HomeEmptyPreview() {
    ParkWatchTheme(darkTheme = true) {
        HomeScreen(onSaveVehicleClick = {})
    }
}

@WearPreviewDevices
@Composable
fun HomeSavedPreview() {
    ParkWatchTheme(darkTheme = true) {
        ParkLocationCard(
            floor = "P1",
            zone = "A",
            row = "1",
            spot = "30",
            elapsedTime = "Estacionado hace 15 min",
        )
    }
}

@WearPreviewDevices
@Composable
fun LocationCardLightPreview() {
    ParkWatchTheme(darkTheme = false) {
        ParkLocationCard(
            floor = "P2",
            zone = "B",
            row = "14",
            spot = "27",
            elapsedTime = "Estacionado hace 42 min",
        )
    }
}

@WearPreviewDevices
@Composable
fun SettingsPreview() {
    ParkWatchTheme(darkTheme = true) {
        SettingsScreen(
            preferences = AppPreferences(
                themeMode = ParkThemeMode.DARK,
                language = ParkLanguage.SPANISH,
            ),
            onThemeSelected = {},
            onLanguageSelected = {},
        )
    }
}

@WearPreviewDevices
@Composable
fun SettingsLightPreview() {
    ParkWatchTheme(darkTheme = false) {
        SettingsScreen(
            preferences = AppPreferences(
                themeMode = ParkThemeMode.LIGHT,
                language = ParkLanguage.ENGLISH,
            ),
            onThemeSelected = {},
            onLanguageSelected = {},
        )
    }
}

@WearPreviewDevices
@Composable
fun LongLocationCardPreview() {
    ParkWatchTheme(darkTheme = true) {
        ParkLocationCard(
            floor = "B12",
            zone = "North-West",
            row = "Row 148",
            spot = "Parking spot 1204",
            elapsedTime = "Parked 2 h 45 min ago",
        )
    }
}

@WearPreviewDevices
@Composable
fun SetTimerPreview() {
    ParkWatchTheme(darkTheme = true) {
        com.david.parkwatch.presentation.timer.SetTimerScreen(
            currentExpiresAt = System.currentTimeMillis() + 45 * 60 * 1000L,
            onSetTimer = {},
        )
    }
}

@WearPreviewDevices
@Composable
fun LocationCardWithTimerPreview() {
    ParkWatchTheme(darkTheme = true) {
        ParkLocationCard(
            floor = "P2",
            zone = "B",
            row = "14",
            spot = "27",
            elapsedTime = "Estacionado hace 25 min",
            timerInfo = "Vence en 35 min",
            isTimerExpired = false,
        )
    }
}
