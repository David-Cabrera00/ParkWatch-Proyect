package com.david.parkwatch.presentation.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.presentation.component.ParkDestructiveButton
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
    val haptic = LocalHapticFeedback.current

    val presets = listOf(
        15 to R.string.timer_preset_15m,
        30 to R.string.timer_preset_30m,
        45 to R.string.timer_preset_45m,
        60 to R.string.timer_preset_1h,
        90 to R.string.timer_preset_1h30,
        120 to R.string.timer_preset_2h,
        180 to R.string.timer_preset_3h,
        240 to R.string.timer_preset_4h,
    )
    val presetPairs = presets.chunked(2)

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
                top = 28.dp,
                bottom = 44.dp,
                start = 10.dp,
                end = 10.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp),
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
                Spacer(modifier = Modifier.size(2.dp))
            }

            items(presetPairs.size) { index ->
                val pair = presetPairs[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    pair.forEach { (minutes, stringRes) ->
                        val buttonColors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = colors.surfaceContainerHigh,
                            contentColor = colors.onSurface,
                        )
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSetTimer(minutes)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            colors = buttonColors,
                            label = {
                                Text(
                                    text = stringResource(stringRes),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            },
                        )
                    }
                }
            }

            if (currentExpiresAt != null) {
                item {
                    Spacer(modifier = Modifier.size(spacing.SpaceXs))
                }
                item {
                    ParkDestructiveButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSetTimer(0)
                        },
                        label = stringResource(R.string.timer_cancel_button),
                    )
                }
            }
        }
    }
}
