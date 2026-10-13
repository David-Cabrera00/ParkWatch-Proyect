package com.david.parkwatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.david.parkwatch.data.repository.ParkingRepository
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.home.HomeScreen
import com.david.parkwatch.presentation.save.SaveParkingScreen

private const val HomeRoute = "home"
private const val SaveRoute = "save"

@Composable
fun ParkWatchNavHost() {
    val context = LocalContext.current
    val repository = remember { ParkingRepository(context) }
    val savedLocation = remember { mutableStateOf<ParkingLocation?>(repository.getParkingLocation()) }
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable(HomeRoute) {
            HomeScreen(
                location = savedLocation.value,
                onSaveVehicleClick = { navController.navigate(SaveRoute) },
                onViewVehicleClick = {
                    // Navegación a detalle de vehículo
                },
            )
        }
        composable(SaveRoute) {
            SaveParkingScreen(
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
