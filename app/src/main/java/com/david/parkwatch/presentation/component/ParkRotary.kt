package com.david.parkwatch.presentation.component

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable

/**
 * Attaches Wear OS rotary scroll behavior (physical rotating crown or bezel)
 * with snap behavior and haptic feedback to a ScalingLazyColumn.
 */
@Composable
fun Modifier.parkRotaryScroll(listState: ScalingLazyListState): Modifier {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    return this
        .focusRequester(focusRequester)
        .focusable()
        .rotaryScrollable(
            behavior = RotaryScrollableDefaults.snapBehavior(listState),
            focusRequester = focusRequester,
        )
}

