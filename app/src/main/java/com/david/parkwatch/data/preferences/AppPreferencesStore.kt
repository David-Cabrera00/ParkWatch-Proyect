package com.david.parkwatch.data.preferences

import android.content.Context

class AppPreferencesStore(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun read(): AppPreferences {
        return AppPreferences(
            themeMode = preferences.getString(KEY_THEME, null)
                ?.let { value -> ParkThemeMode.entries.firstOrNull { it.name == value } }
                ?: ParkThemeMode.SYSTEM,
            language = preferences.getString(KEY_LANGUAGE, null)
                ?.let { value -> ParkLanguage.entries.firstOrNull { it.name == value } }
                ?: ParkLanguage.SYSTEM,
        )
    }

    fun save(preferences: AppPreferences) {
        this.preferences.edit()
            .putString(KEY_THEME, preferences.themeMode.name)
            .putString(KEY_LANGUAGE, preferences.language.name)
            .apply()
    }

    private companion object {
        const val FILE_NAME = "parkwatch_preferences"
        const val KEY_THEME = "theme_mode"
        const val KEY_LANGUAGE = "language"
    }
}
