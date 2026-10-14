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
import androidx.compose.ui.text.style.TextOverflow
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
            .padding(spacing.SpaceMd),
        verticalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
    ) {
        Text(
            text = floor,
            style = MaterialTheme.typography.numeralLarge,
            color = colors.primary,
            maxLines = 1,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Zona", style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant, maxLines = 1)
                Text(zone, style = MaterialTheme.typography.titleSmall, color = colors.onSurface, maxLines = 1)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Fila", style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant, maxLines = 1)
                Text(row, style = MaterialTheme.typography.titleSmall, color = colors.onSurface, maxLines = 1)
            }
            if (!spot.isNullOrBlank()) {
                Column(modifier = Modifier.weight(1.2f)) {
                    Text("Puesto", style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant, maxLines = 1)
                    Text(spot, style = MaterialTheme.typography.titleSmall, color = colors.onSurface, maxLines = 1)
                }
            }
        }
        if (!elapsedTime.isNullOrBlank()) {
            Spacer(modifier = Modifier.size(spacing.SpaceXs))
            Text(
                text = elapsedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
