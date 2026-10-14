package com.david.parkwatch.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun ParkLocationCard(
    floor: String,
    zone: String,
    row: String,
    spot: String? = null,
    elapsedTime: String? = null,
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    val spacing = parkSpacing()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surfaceContainer)
            .padding(spacing.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
    ) {
        Text(floor, style = MaterialTheme.typography.numeralLarge, color = colors.primary)
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.SpaceXl)) {
            Column {
                Text("Zona", style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
                Text(zone, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            }
            Column {
                Text("Fila", style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
                Text(row, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            }
            if (!spot.isNullOrBlank()) {
                Column {
                    Text("Puesto", style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
                    Text(spot, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
                }
            }
        }
        if (!elapsedTime.isNullOrBlank()) {
            Spacer(modifier = Modifier.size(spacing.SpaceXs))
            Text(elapsedTime, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        }
    }
}
