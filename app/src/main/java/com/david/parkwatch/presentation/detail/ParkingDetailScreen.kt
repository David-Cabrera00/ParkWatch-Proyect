package com.david.parkwatch.presentation.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.presentation.component.ParkDestructiveButton
import com.david.parkwatch.presentation.component.ParkLocationCard
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun ParkingDetailScreen(
    location: ParkingLocation,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val context = LocalContext.current
    val isConfirmingDelete = remember { mutableStateOf(false) }

    BackHandler {
        if (isConfirmingDelete.value) {
            isConfirmingDelete.value = false
        } else {
            onNavigateBack()
        }
    }

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(
                top = spacing.SpaceMd,
                bottom = spacing.SpaceXl,
                start = spacing.SpaceMd,
                end = spacing.SpaceMd,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.SpaceXs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!isConfirmingDelete.value) {
                item {
                    Text(
                        text = stringResource(R.string.detail_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    ParkLocationCard(
                        floor = location.floor,
                        zone = location.zone,
                        row = location.row,
                        spot = location.spot,
                        elapsedTime = formatElapsedTime(location.parkedAt),
                    )
                }
                if (location.latitude != null && location.longitude != null) {
                    item {
                        Text(
                            text = "Ã°Å¸â€œÂ " + stringResource(R.string.save_parking_gps_captured),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    item {
                        ParkSecondaryButton(
                            onClick = {
                                val uri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(VehÃƒÂ­culo)")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                try {
                                    context.startActivity(mapIntent)
                                } catch (_: ActivityNotFoundException) {
                                }
                            },
                            label = stringResource(R.string.detail_open_map),
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    ParkSecondaryButton(
                        onClick = onEditClick,
                        label = stringResource(R.string.detail_edit),
                    )
                }
                item {
                    ParkDestructiveButton(
                        onClick = { isConfirmingDelete.value = true },
                        label = stringResource(R.string.detail_delete),
                    )
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.delete_confirm_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.delete_confirm_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    ParkDestructiveButton(
                        onClick = onDeleteConfirmed,
                        label = stringResource(R.string.delete_confirm_delete),
                    )
                }
                item {
                    ParkSecondaryButton(
                        onClick = { isConfirmingDelete.value = false },
                        label = stringResource(R.string.delete_confirm_cancel),
                    )
                }
            }
        }
    }
}

@Composable
private fun formatElapsedTime(parkedAt: Long): String {
    val now = System.currentTimeMillis()
    val elapsedMillis = (now - parkedAt).coerceAtLeast(0L)
    val elapsedMinutes = elapsedMillis / (1000 * 60)
    val hours = elapsedMinutes / 60
    val minutes = elapsedMinutes % 60

    return when {
        elapsedMinutes < 1 -> stringResource(R.string.detail_time_just_now)
        hours < 1 -> stringResource(R.string.detail_time_minutes, elapsedMinutes)
        else -> stringResource(R.string.detail_time_hours_minutes, hours, minutes)
    }
}
