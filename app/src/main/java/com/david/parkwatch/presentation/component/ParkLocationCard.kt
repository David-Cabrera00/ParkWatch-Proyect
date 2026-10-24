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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material3.Icon
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
    isTimerExpired: Boolean = false,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = floor,
                style = MaterialTheme.typography.numeralLarge,
                color = colors.primary,
                maxLines = 1,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(com.david.parkwatch.R.string.location_zone), style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant, maxLines = 1)
                Text(zone, style = MaterialTheme.typography.titleSmall, color = colors.onSurface, maxLines = 1)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(com.david.parkwatch.R.string.location_row), style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant, maxLines = 1)
                Text(row, style = MaterialTheme.typography.titleSmall, color = colors.onSurface, maxLines = 1)
            }
        }
        if (!spot.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
            ) {
                Text(stringResource(com.david.parkwatch.R.string.location_spot), style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant, maxLines = 1)
                Text(
                    text = spot,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
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
        if (!timerInfo.isNullOrBlank()) {
            Spacer(modifier = Modifier.size(spacing.SpaceXs))
            Text(
                text = timerInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isTimerExpired) colors.error else colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
