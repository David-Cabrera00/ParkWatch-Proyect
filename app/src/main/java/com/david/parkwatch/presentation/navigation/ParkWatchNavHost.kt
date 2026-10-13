package com.david.parkwatch.presentation.navigation

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
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.parser.ParsedParkingInfo
import com.david.parkwatch.domain.parser.VoiceParkingParser
import com.david.parkwatch.presentation.detail.ParkingDetailScreen
import com.david.parkwatch.presentation.home.HomeScreen
import com.david.parkwatch.presentation.save.SaveParkingScreen

private const val HomeRoute = "home"
private const val SaveRoute = "save"
private const val DetailRoute = "detail"
private const val EditRoute = "edit"

@Composable
fun ParkWatchNavHost() {
    val context = LocalContext.current
    val repository = remember { ParkingRepository(context) }
    val savedLocation = remember { mutableStateOf<ParkingLocation?>(repository.getParkingLocation()) }
    val voiceParsedInfo = remember { mutableStateOf<ParsedParkingInfo?>(null) }
    val navController = rememberSwipeDismissableNavController()

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
            )
        }
        composable(SaveRoute) {
            SaveParkingScreen(
                initialParsed = voiceParsedInfo.value,
                onSaveSuccess = { location ->
                    repository.saveParkingLocation(location)
                    savedLocation.value = location
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
                    onDeleteConfirmed = {
                        repository.clearParkingLocation()
                        savedLocation.value = null
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
        composable(EditRoute) {
            SaveParkingScreen(
                initialLocation = savedLocation.value,
                onSaveSuccess = { location ->
                    repository.saveParkingLocation(location)
                    savedLocation.value = location
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
