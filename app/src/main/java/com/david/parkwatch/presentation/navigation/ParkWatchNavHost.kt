package com.david.parkwatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.david.parkwatch.presentation.home.HomeScreen
import com.david.parkwatch.presentation.save.SaveParkingScreen

private const val HomeRoute = "home"
private const val SaveRoute = "save"

@Composable
fun ParkWatchNavHost() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable(HomeRoute) {
            HomeScreen(onSaveVehicleClick = { navController.navigate(SaveRoute) })
        }
        composable(SaveRoute) {
            SaveParkingScreen()
        }
    }
}
