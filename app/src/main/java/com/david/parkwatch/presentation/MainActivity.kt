package com.david.parkwatch.presentation

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.david.parkwatch.data.preferences.AppPreferences
import com.david.parkwatch.data.preferences.AppPreferencesStore
import com.david.parkwatch.data.preferences.ParkLanguage
import com.david.parkwatch.data.preferences.ParkThemeMode
import com.david.parkwatch.presentation.navigation.ParkWatchNavHost
import com.david.parkwatch.presentation.theme.ParkWatchTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val store = AppPreferencesStore(newBase)
        val preferences = store.read()
        val localized = newBase.withLanguage(preferences.language)
        super.attachBaseContext(localized)
    }

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

    ParkWatchTheme(darkTheme = darkTheme) {
        ParkWatchNavHost(
            preferences = preferences,
            onPreferencesChanged = { updated ->
                val languageChanged = updated.language != preferences.language
                store.save(updated)
                preferences = updated

                if (languageChanged) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            val localeManager = context.getSystemService(LocaleManager::class.java)
                            val localeList = when (updated.language) {
                                ParkLanguage.SYSTEM -> LocaleList.getEmptyLocaleList()
                                ParkLanguage.SPANISH -> LocaleList.forLanguageTags("es")
                                ParkLanguage.ENGLISH -> LocaleList.forLanguageTags("en")
                            }
                            localeManager?.applicationLocales = localeList
                        } catch (_: Exception) {
                        }
                    }
                    context.findActivity()?.recreate()
                }
            },
        )
    }
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private fun Context.withLanguage(language: ParkLanguage): Context {
    if (language == ParkLanguage.SYSTEM) return this
    val locale = if (language == ParkLanguage.ENGLISH) Locale.ENGLISH else Locale.forLanguageTag("es")
    Locale.setDefault(locale)
    val configuration = Configuration(resources.configuration).apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
    return createConfigurationContext(configuration)
}

@androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
@androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
