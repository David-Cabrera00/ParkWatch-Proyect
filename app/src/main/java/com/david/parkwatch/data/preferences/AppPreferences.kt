package com.david.parkwatch.data.preferences

enum class ParkThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class ParkLanguage {
    SYSTEM,
    SPANISH,
    ENGLISH,
}

enum class ParkAccentColor {
    EMERALD,
    CYAN,
    AMBER,
    RUBY,
}

data class AppPreferences(
    val themeMode: ParkThemeMode = ParkThemeMode.SYSTEM,
    val language: ParkLanguage = ParkLanguage.SYSTEM,
    val accentColor: ParkAccentColor = ParkAccentColor.EMERALD,
)
