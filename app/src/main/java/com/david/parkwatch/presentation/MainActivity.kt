package com.david.parkwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.david.parkwatch.presentation.home.HomeScreen
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
    ParkWatchTheme {
        HomeScreen(onSaveVehicleClick = { /* TODO: Navigation to save screen */ })
    }
}

@androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
@androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}