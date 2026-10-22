package com.david.parkwatch.presentation.navigation

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.david.parkwatch.R
import com.david.parkwatch.data.location.LocationHelper
import com.david.parkwatch.data.preferences.AppPreferences
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.data.timer.ParkingTimerManager
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.parser.ParsedParkingInfo
import com.david.parkwatch.domain.parser.VoiceParkingParser
import com.david.parkwatch.presentation.complication.ParkWatchComplicationManager
import com.david.parkwatch.presentation.detail.ParkingDetailScreen
import com.david.parkwatch.presentation.home.HomeScreen
import com.david.parkwatch.presentation.save.SaveParkingScreen
import com.david.parkwatch.presentation.settings.SettingsScreen
import com.david.parkwatch.presentation.tile.ParkWatchTileManager
import com.david.parkwatch.presentation.timer.SetTimerScreen
import java.util.UUID

private const val HomeRoute = "home"
private const val SaveRoute = "save"
private const val DetailRoute = "detail"
private const val EditRoute = "edit"
private const val SettingsRoute = "settings"
private const val TimerRoute = "timer"

@Composable
fun ParkWatchNavHost(
    preferences: AppPreferences,
    onPreferencesChanged: (AppPreferences) -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { ParkingRepository(context) }
    val locationHelper = remember { LocationHelper(context) }
    val savedLocation = remember { mutableStateOf<ParkingLocation?>(repository.getParkingLocation()) }
    val voiceParsedInfo = remember { mutableStateOf<ParsedParkingInfo?>(null) }
    val navController = rememberSwipeDismissableNavController()

    fun performQuickSave(coords: Pair<Double, Double>?) {
        val now = System.currentTimeMillis()
        val quickLocation = ParkingLocation(
            id = UUID.randomUUID().toString(),
            floor = "GPS",
            zone = "—",
            row = "—",
            latitude = coords?.first,
            longitude = coords?.second,
            parkedAt = now,
            createdAt = now,
            updatedAt = now,
        )
        repository.saveParkingLocation(quickLocation)
        savedLocation.value = quickLocation
        ParkWatchTileManager.requestUpdate(context)
        ParkWatchComplicationManager.requestUpdate(context)
        navController.navigate(DetailRoute)
    }

    val quickSavePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val coords = locationHelper.getLastKnownCoordinates()
        performQuickSave(coords)
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()

            if (!spokenText.isNullOrBlank()) {
                val parsed = VoiceParkingParser.parse(spokenText)
                if (parsed.hasData) {
                    voiceParsedInfo.value = parsed
                    navController.navigate(SaveRoute)
                }
            }
        }
    }

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable(HomeRoute) {
            HomeScreen(
                location = savedLocation.value,
                onSaveVehicleClick = {
                    voiceParsedInfo.value = null
                    navController.navigate(SaveRoute)
                },
                onQuickSaveClick = {
                    if (locationHelper.hasLocationPermission()) {
                        performQuickSave(locationHelper.getLastKnownCoordinates())
                    } else {
                        quickSavePermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                    }
                },
                onVoiceClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(
                            RecognizerIntent.EXTRA_PROMPT,
                            context.getString(R.string.voice_prompt)
                        )
                    }
                    try {
                        speechLauncher.launch(intent)
                    } catch (e: ActivityNotFoundException) {
                        voiceParsedInfo.value = null
                        navController.navigate(SaveRoute)
                    }
                },
                onViewVehicleClick = { navController.navigate(DetailRoute) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
            )
        }
        composable(SaveRoute) {
            SaveParkingScreen(
                initialParsed = voiceParsedInfo.value,
                onSaveSuccess = { location ->
                    repository.saveParkingLocation(location)
                    savedLocation.value = location
                    ParkWatchTileManager.requestUpdate(context)
                    ParkWatchComplicationManager.requestUpdate(context)
                    voiceParsedInfo.value = null
                },
                onNavigateBack = {
                    voiceParsedInfo.value = null
                    navController.popBackStack()
                },
            )
        }
        composable(DetailRoute) {
            val location = savedLocation.value
            if (location != null) {
                ParkingDetailScreen(
                    location = location,
                    onEditClick = { navController.navigate(EditRoute) },
                    onSetTimerClick = { navController.navigate(TimerRoute) },
                    onDeleteConfirmed = {
                        ParkingTimerManager.cancelTimer(context)
                        repository.clearParkingLocation()
                        savedLocation.value = null
                        ParkWatchTileManager.requestUpdate(context)
                        ParkWatchComplicationManager.requestUpdate(context)
                        navController.popBackStack(HomeRoute, inclusive = false)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                )
            } else {
                navController.popBackStack(HomeRoute, inclusive = false)
            }
        }
        composable(TimerRoute) {
            val location = savedLocation.value
            if (location != null) {
                SetTimerScreen(
                    currentExpiresAt = location.expiresAt,
                    onSetTimer = { durationMinutes ->
                        val newExpiresAt = if (durationMinutes > 0) {
                            System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
                        } else {
                            null
                        }
                        val updated = location.copy(
                            expiresAt = newExpiresAt,
                            updatedAt = System.currentTimeMillis(),
                        )
                        repository.saveParkingLocation(updated)
                        savedLocation.value = updated
                        if (newExpiresAt != null) {
                            ParkingTimerManager.scheduleTimer(context, newExpiresAt)
                        } else {
                            ParkingTimerManager.cancelTimer(context)
                        }
                        ParkWatchTileManager.requestUpdate(context)
                        ParkWatchComplicationManager.requestUpdate(context)
                        navController.popBackStack()
                    },
                )
            } else {
                navController.popBackStack(HomeRoute, inclusive = false)
            }
        }
        composable(EditRoute) {
            SaveParkingScreen(
                initialLocation = savedLocation.value,
                onSaveSuccess = { location ->
                    repository.saveParkingLocation(location)
                    savedLocation.value = location
                    ParkWatchTileManager.requestUpdate(context)
                    ParkWatchComplicationManager.requestUpdate(context)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }
        composable(SettingsRoute) {
            SettingsScreen(
                preferences = preferences,
                onThemeSelected = { mode -> onPreferencesChanged(preferences.copy(themeMode = mode)) },
                onLanguageSelected = { language -> onPreferencesChanged(preferences.copy(language = language)) },
            )
        }
    }
}
