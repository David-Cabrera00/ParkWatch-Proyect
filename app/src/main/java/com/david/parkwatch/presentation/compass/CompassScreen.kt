package com.david.parkwatch.presentation.compass

import android.hardware.GeomagneticField
import android.location.Location
import android.location.LocationListener
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.data.location.LocationHelper
import com.david.parkwatch.data.sensor.CompassSensorManager
import com.david.parkwatch.domain.model.VehicleType
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.presentation.component.ParkSecondaryButton
import com.david.parkwatch.presentation.model.iconRes
import com.david.parkwatch.presentation.model.labelRes
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing
import com.david.parkwatch.presentation.util.ParkHaptics
import kotlin.math.cos
import kotlin.math.sin

private const val ARRIVAL_THRESHOLD_METERS = 10f

@Composable
fun CompassScreen(
    targetLatitude: Double,
    targetLongitude: Double,
    vehicleType: VehicleType,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onClearLocation: () -> Unit = {},
) {
    val context = LocalContext.current
    val colors = parkColors()
    val spacing = parkSpacing()
    val haptic = LocalHapticFeedback.current

    val locationHelper = remember { LocationHelper(context) }
    val sensorManager = remember { CompassSensorManager(context) }

    var userLocation by remember { mutableStateOf<Location?>(null) }
    var currentAzimuth by remember { mutableFloatStateOf(0f) }
    var distanceMeters by remember { mutableStateOf<Float?>(null) }
    var targetBearing by remember { mutableStateOf<Float?>(null) }
    var hasTriggeredArrivalHaptic by remember { mutableStateOf(false) }

    var continuousAngle by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val initialCoords = locationHelper.getLastKnownCoordinates()
        if (initialCoords != null) {
            val (dist, bearing) = LocationHelper.calculateDistanceAndBearing(
                initialCoords.first,
                initialCoords.second,
                targetLatitude,
                targetLongitude,
            )
            distanceMeters = dist
            targetBearing = bearing
        }

        var locationListener: LocationListener? = null
        locationListener = locationHelper.requestLocationUpdates(
            minTimeMs = 1000L,
            minDistanceMeters = 1f,
        ) { location ->
            userLocation = location
            val (dist, bearing) = LocationHelper.calculateDistanceAndBearing(
                location.latitude,
                location.longitude,
                targetLatitude,
                targetLongitude,
            )
            distanceMeters = dist
            targetBearing = bearing
        }

        sensorManager.onAzimuthChanged = { azimuth ->
            val declination = userLocation?.let {
                val geoField = GeomagneticField(
                    it.latitude.toFloat(),
                    it.longitude.toFloat(),
                    it.altitude.toFloat(),
                    System.currentTimeMillis(),
                )
                geoField.declination
            } ?: 0f

            currentAzimuth = (azimuth + declination + 360f) % 360f
        }
        sensorManager.startListening()

        onDispose {
            sensorManager.stopListening()
            locationHelper.removeLocationUpdates(locationListener)
        }
    }

    // Relative angle: how much the needle should point relative to the watch's heading
    val relativeAngle = if (targetBearing != null) {
        (targetBearing!! - currentAzimuth + 360f) % 360f
    } else {
        0f
    }

    LaunchedEffect(relativeAngle) {
        var delta = (relativeAngle - continuousAngle) % 360f
        if (delta > 180f) delta -= 360f
        if (delta < -180f) delta += 360f
        continuousAngle += delta
    }

    val animatedAngle by animateFloatAsState(
        targetValue = continuousAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "compassNeedleAngle",
    )

    // Trigger arrival haptic once
    val isArrived = (distanceMeters ?: Float.MAX_VALUE) <= ARRIVAL_THRESHOLD_METERS
    LaunchedEffect(isArrived) {
        if (isArrived && !hasTriggeredArrivalHaptic) {
            hasTriggeredArrivalHaptic = true
            ParkHaptics.playArrival(context)
        }
    }

    var isConfirmingFinish by remember { mutableStateOf(false) }

    BackHandler {
        if (isConfirmingFinish) {
            isConfirmingFinish = false
        } else {
            onNavigateBack()
        }
    }

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        if (isConfirmingFinish) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 14.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.compass_finish_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.primary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(spacing.SpaceXs))
                Text(
                    text = stringResource(R.string.compass_finish_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(spacing.SpaceMd))
                ParkPrimaryButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClearLocation()
                    },
                    label = stringResource(R.string.compass_finish_confirm),
                )
                Spacer(modifier = Modifier.size(spacing.SpaceXs))
                ParkSecondaryButton(
                    onClick = { isConfirmingFinish = false },
                    label = stringResource(R.string.delete_confirm_cancel),
                )
            }
        } else if (isArrived) {
            val infiniteTransition = rememberInfiniteTransition(label = "arrivalPulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "pulseScale",
            )
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 0.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "pulseAlpha",
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 14.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Halo de pulso
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = pulseAlpha)),
                    )
                    // Badge central
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(vehicleType.iconRes),
                            contentDescription = stringResource(vehicleType.labelRes),
                            modifier = Modifier.size(28.dp),
                            tint = colors.onPrimaryContainer,
                        )
                    }
                }
                Spacer(modifier = Modifier.size(spacing.SpaceSm))
                Text(
                    text = stringResource(R.string.compass_arrived_destination, stringResource(vehicleType.labelRes)),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.primary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                Text(
                    text = stringResource(R.string.compass_arrived_short),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(spacing.SpaceMd))
                ParkPrimaryButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isConfirmingFinish = true
                    },
                    label = stringResource(R.string.compass_free_spot),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                // Dial Canvas: Outer circle, cardinal ticks and needle
                CompassDial(
                    needleAngle = animatedAngle,
                    isArrived = false,
                    primaryColor = colors.primary,
                    secondaryColor = colors.secondary,
                    surfaceColor = colors.surfaceContainer,
                    outlineColor = colors.surfaceContainerHigh,
                    modifier = Modifier.fillMaxSize(),
                )

                // Center Vehicle Hub (clickable to finish guidance anytime)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            isConfirmingFinish = true
                        }
                        .background(colors.surfaceContainerHigh),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(vehicleType.iconRes),
                        contentDescription = stringResource(vehicleType.labelRes),
                        modifier = Modifier.size(20.dp),
                        tint = colors.onSurface,
                    )
                }

                // Top Header & Bottom Distance Labels
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Header
                    Text(
                        text = stringResource(R.string.compass_heading_to, stringResource(vehicleType.labelRes)),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom Status
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        if (distanceMeters != null) {
                            val dist = distanceMeters!!
                            val formattedDist = if (dist < 1000f) {
                                stringResource(R.string.compass_distance_meters, dist.toInt())
                            } else {
                                stringResource(R.string.compass_distance_km, dist / 1000f)
                            }
                            Text(
                                text = formattedDist,
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.primary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            val cardinalKey = targetBearing?.let { LocationHelper.getCardinalDirectionKey(it) }
                            val cardinalString = cardinalKey?.let { getCardinalString(it) }.orEmpty()
                            if (cardinalString.isNotBlank()) {
                                Text(
                                    text = cardinalString,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                )
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.compass_waiting_gps),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompassDial(
    needleAngle: Float,
    isArrived: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    surfaceColor: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .rotate(needleAngle),
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.minDimension / 2f) - 36.dp.toPx()

        // Outer Ring
        drawCircle(
            color = outlineColor,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx()),
        )

        // Cardinal Ticks
        for (i in 0 until 12) {
            val angleDeg = i * 30.0
            val angleRad = Math.toRadians(angleDeg)
            val isMajor = i % 3 == 0
            val tickLength = if (isMajor) 8.dp.toPx() else 4.dp.toPx()
            val startDist = radius - tickLength
            val startX = center.x + (startDist * sin(angleRad)).toFloat()
            val startY = center.y - (startDist * cos(angleRad)).toFloat()
            val endX = center.x + (radius * sin(angleRad)).toFloat()
            val endY = center.y - (radius * cos(angleRad)).toFloat()

            drawLine(
                color = if (isMajor) secondaryColor else outlineColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx(),
            )
        }

        // Arrow Needle (pointing straight UP, which will be rotated by needleAngle)
        if (!isArrived) {
            val needleTipRadius = radius - 12.dp.toPx()
            val needleBaseWidth = 14.dp.toPx()
            val needleHubRadius = 24.dp.toPx()

            // North/Target Arrow (Pointer)
            val path = Path().apply {
                moveTo(center.x, center.y - needleTipRadius)
                lineTo(center.x + needleBaseWidth / 2f, center.y - needleHubRadius)
                lineTo(center.x - needleBaseWidth / 2f, center.y - needleHubRadius)
                close()
            }
            drawPath(path, color = primaryColor)

            // Tail indicator
            val tailTipRadius = needleHubRadius + 14.dp.toPx()
            val tailPath = Path().apply {
                moveTo(center.x, center.y + tailTipRadius)
                lineTo(center.x + (needleBaseWidth / 2.5f), center.y + needleHubRadius)
                lineTo(center.x - (needleBaseWidth / 2.5f), center.y + needleHubRadius)
                close()
            }
            drawPath(tailPath, color = surfaceColor)
        }
    }
}

@Composable
private fun getCardinalString(cardinalKey: String): String {
    return when (cardinalKey) {
        "N" -> stringResource(R.string.compass_dir_n)
        "NE" -> stringResource(R.string.compass_dir_ne)
        "E" -> stringResource(R.string.compass_dir_e)
        "SE" -> stringResource(R.string.compass_dir_se)
        "S" -> stringResource(R.string.compass_dir_s)
        "SW" -> stringResource(R.string.compass_dir_sw)
        "W" -> stringResource(R.string.compass_dir_w)
        "NW" -> stringResource(R.string.compass_dir_nw)
        else -> ""
    }
}

