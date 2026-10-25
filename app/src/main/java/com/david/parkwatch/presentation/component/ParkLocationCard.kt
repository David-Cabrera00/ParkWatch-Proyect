package com.david.parkwatch.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.david.parkwatch.domain.model.VehicleType
import com.david.parkwatch.presentation.model.iconRes
import com.david.parkwatch.presentation.model.labelRes
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun ParkLocationCard(
    floor: String,
    zone: String,
    row: String,
    spot: String? = null,
    vehicleType: VehicleType = VehicleType.CAR,
    elapsedTime: String? = null,
    timerInfo: String? = null,
    timerProgress: Float? = null,
    isTimerExpired: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val shape = RoundedCornerShape(22.dp)

    val borderStroke = BorderStroke(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                colors.primary.copy(alpha = 0.45f),
                colors.surfaceContainerHigh.copy(alpha = 0.15f),
            ),
        ),
    )

    val cardModifier = modifier
        .fillMaxWidth()
        .clip(shape)
        .then(
            if (onClick != null) {
                Modifier.clickable(role = Role.Button, onClick = onClick)
            } else {
                Modifier
            }
        )
        .background(colors.surfaceContainer)
        .border(borderStroke, shape)
        .padding(horizontal = 12.dp, vertical = 10.dp)

    Column(
        modifier = cardModifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Cabecera: Piso y Badge de Vehículo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = floor.ifEmpty { "—" },
                style = MaterialTheme.typography.displaySmall,
                color = colors.primary,
                maxLines = 1,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer.copy(alpha = 0.35f))
                    .border(
                        BorderStroke(0.5.dp, colors.primary.copy(alpha = 0.3f)),
                        RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Icon(
                    painter = painterResource(vehicleType.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = colors.primary,
                )
                Text(
                    text = stringResource(vehicleType.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurface,
                )
            }
        }

        // Dashboard Grid: Cápsulas de métricas (Zona, Fila, Puesto)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MetricPill(
                label = stringResource(com.david.parkwatch.R.string.location_zone),
                value = zone.ifEmpty { "—" },
                modifier = Modifier.weight(1f),
            )
            MetricPill(
                label = stringResource(com.david.parkwatch.R.string.location_row),
                value = row.ifEmpty { "—" },
                modifier = Modifier.weight(1f),
            )
            if (!spot.isNullOrBlank()) {
                MetricPill(
                    label = stringResource(com.david.parkwatch.R.string.location_spot),
                    value = spot,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Tiempo transcurrido
        if (!elapsedTime.isNullOrBlank()) {
            Text(
                text = elapsedTime,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Temporizador de parquímetro con indicador de progreso circular
        if (!timerInfo.isNullOrBlank()) {
            val progressColor = if (isTimerExpired || (timerProgress ?: 1f) <= 0.2f) colors.error else colors.primary
            val containerColor = if (isTimerExpired) colors.errorContainer.copy(alpha = 0.35f) else colors.primaryContainer.copy(alpha = 0.25f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(containerColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (timerProgress != null) {
                    val trackColor = colors.surfaceContainerHigh
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawCircle(
                            color = trackColor,
                            style = Stroke(width = 2.dp.toPx()),
                        )
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = 360f * timerProgress,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                    Spacer(modifier = Modifier.size(6.dp))
                }
                Text(
                    text = timerInfo,
                    style = MaterialTheme.typography.labelSmall,
                    color = progressColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceContainerHigh.copy(alpha = 0.7f))
            .padding(vertical = 4.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                maxLines = 1,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
