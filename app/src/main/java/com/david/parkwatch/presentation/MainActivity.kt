package com.david.parkwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.CompositionLocalProvider
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import com.david.parkwatch.data.preferences.AppPreferences
import com.david.parkwatch.data.preferences.AppPreferencesStore
import com.david.parkwatch.data.preferences.ParkLanguage
import com.david.parkwatch.data.preferences.ParkThemeMode
import com.david.parkwatch.presentation.navigation.ParkWatchNavHost
import com.david.parkwatch.presentation.theme.ParkWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val context = LocalContext.current
    val store = remember { AppPreferencesStore(context) }
    var preferences by remember { mutableStateOf(store.read()) }
    val darkTheme = when (preferences.themeMode) {
        ParkThemeMode.SYSTEM -> isSystemInDarkTheme()
        ParkThemeMode.LIGHT -> false
        ParkThemeMode.DARK -> true
    }
    val localizedContext = remember(preferences.language) {
        context.withLanguage(preferences.language)
    }

    ParkWatchTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(LocalContext provides localizedContext) {
            ParkWatchNavHost(
                preferences = preferences,
                onPreferencesChanged = { updated ->
                    store.save(updated)
                    preferences = updated
                },
            )
        }
    }
}

private fun Context.withLanguage(language: ParkLanguage): Context {
    if (language == ParkLanguage.SYSTEM) return this
    val locale = if (language == ParkLanguage.ENGLISH) Locale.ENGLISH else Locale("es")
    val configuration = Configuration(resources.configuration).apply { setLocale(locale) }
    return createConfigurationContext(configuration)
}

@androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
@androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
