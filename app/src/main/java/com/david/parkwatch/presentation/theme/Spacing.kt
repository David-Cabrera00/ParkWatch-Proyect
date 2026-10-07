package com.david.parkwatch.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@androidx.compose.runtime.Stable
interface ParkSpacing {
    val SpaceXs: androidx.compose.ui.unit.Dp
    val SpaceSm: androidx.compose.ui.unit.Dp
    val SpaceMd: androidx.compose.ui.unit.Dp
    val SpaceLg: androidx.compose.ui.unit.Dp
    val SpaceXl: androidx.compose.ui.unit.Dp
}

@androidx.compose.runtime.Stable
class ParkSpacingImpl : ParkSpacing {
    override val SpaceXs = 4.dp
    override val SpaceSm = 8.dp
    override val SpaceMd = 12.dp
    override val SpaceLg = 16.dp
    override val SpaceXl = 24.dp
}

internal val LocalParkSpacing = staticCompositionLocalOf { ParkSpacingImpl() }

@androidx.compose.runtime.Composable
fun parkSpacing(): ParkSpacing = LocalParkSpacing.current