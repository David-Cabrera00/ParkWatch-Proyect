package com.david.parkwatch.data.preferences

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class AppPreferencesStoreTest {

    private lateinit var context: Context
    private lateinit var store: AppPreferencesStore

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        store = AppPreferencesStore(context)
    }

    @Test
    fun read_returnsDefaults_whenNothingWasSaved() {
        val preferences = store.read()

        assertEquals(ParkThemeMode.SYSTEM, preferences.themeMode)
        assertEquals(ParkLanguage.SYSTEM, preferences.language)
    }

    @Test
    fun saveAndRead_roundTripsThemeAndLanguage() {
        val preferences = AppPreferences(
            themeMode = ParkThemeMode.DARK,
            language = ParkLanguage.ENGLISH,
        )

        store.save(preferences)

        assertEquals(preferences, store.read())
    }

    @Test
    fun saveAndRead_supportsEveryThemeAndLanguageCombination() {
        ParkThemeMode.entries.forEach { theme ->
            ParkLanguage.entries.forEach { language ->
                val expected = AppPreferences(themeMode = theme, language = language)

                store.save(expected)

                assertEquals(expected, store.read())
            }
        }
    }

    @Test
    fun read_returnsDefaults_whenStoredValuesAreInvalid() {
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, "UNKNOWN_THEME")
            .putString(KEY_LANGUAGE, "UNKNOWN_LANGUAGE")
            .commit()

        val preferences = store.read()

        assertEquals(ParkThemeMode.SYSTEM, preferences.themeMode)
        assertEquals(ParkLanguage.SYSTEM, preferences.language)
    }

    private companion object {
        const val PREFERENCES_FILE = "parkwatch_preferences"
        const val KEY_THEME = "theme_mode"
        const val KEY_LANGUAGE = "language"
    }
}
