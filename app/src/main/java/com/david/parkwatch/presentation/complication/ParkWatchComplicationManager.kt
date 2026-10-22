package com.david.parkwatch.presentation.complication

import android.content.ComponentName
import android.content.Context
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

object ParkWatchComplicationManager {
    fun requestUpdate(context: Context) {
        try {
            ComplicationDataSourceUpdateRequester.create(
                context = context,
                complicationDataSourceComponent = ComponentName(context, ParkWatchComplicationService::class.java),
            ).requestUpdateAll()
        } catch (_: Exception) {
            // Safe fallback if complication component is not active
        }
    }
}

