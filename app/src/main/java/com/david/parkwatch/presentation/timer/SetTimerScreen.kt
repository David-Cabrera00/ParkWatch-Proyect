package com.david.parkwatch.presentation.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.presentation.component.ParkDestructiveButton
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.component.parkRotaryScroll
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun SetTimerScreen(
    currentExpiresAt: Long?,
    modifier: Modifier = Modifier,
    onSetTimer: (durationMinutes: Int) -> Unit,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val listState = rememberScalingLazyListState()

    val presets = listOf(
        15 to R.string.timer_preset_15m,
        30 to R.string.timer_preset_30m,
        45 to R.string.timer_preset_45m,
        60 to R.string.timer_preset_1h,
        90 to R.string.timer_preset_1h30,
        120 to R.string.timer_preset_2h,
        180 to R.string.timer_preset_3h,
    )

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .parkRotaryScroll(listState),
            contentPadding = PaddingValues(
                top = spacing.SpaceMd,
                bottom = spacing.SpaceXl,
                start = spacing.SpaceMd,
                end = spacing.SpaceMd,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.SpaceXs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = stringResource(R.string.timer_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.primary,
                    textAlign = TextAlign.Center,
                )
            }
            item {
                Text(
                    text = stringResource(R.string.timer_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            item {
                Spacer(modifier = Modifier.size(spacing.SpaceXs))
            }

            items(presets.size) { index ->
                val (minutes, stringRes) = presets[index]
                ParkSecondaryButton(
                    onClick = { onSetTimer(minutes) },
                    label = stringResource(stringRes),
                )
            }

            if (currentExpiresAt != null) {
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceSm))
                }
                item {
                    ParkDestructiveButton(
                        onClick = { onSetTimer(0) },
                        label = stringResource(R.string.timer_cancel_button),
                    )
                }
            }
        }
    }
}

