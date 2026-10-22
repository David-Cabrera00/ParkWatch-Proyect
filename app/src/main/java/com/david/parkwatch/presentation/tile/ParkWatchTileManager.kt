package com.david.parkwatch.presentation.tile

import android.content.Context
import androidx.wear.tiles.TileService

object ParkWatchTileManager {
    fun requestUpdate(context: Context) {
        try {
            TileService.getUpdater(context).requestUpdate(ParkWatchTileService::class.java)
        } catch (_: Exception) {
            // Safe fallback if tiles provider service isn't active
        }
    }
}

