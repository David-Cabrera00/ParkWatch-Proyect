package com.david.parkwatch.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.Role
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
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    val spacing = parkSpacing()

    val cardModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .background(colors.surfaceContainer)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    } else {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surfaceContainer)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    }

    Column(
        modifier = cardModifier,
        verticalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = floor,
                style = MaterialTheme.typography.displaySmall,
                color = colors.primary,
                maxLines = 1,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
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
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(com.david.parkwatch.R.string.location_zone),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                )
                Text(
                    text = zone,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    maxLines = 1,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(com.david.parkwatch.R.string.location_row),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                )
                Text(
                    text = row,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    maxLines = 1,
                )
            }
            if (!spot.isNullOrBlank()) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = stringResource(com.david.parkwatch.R.string.location_spot),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                    )
                    Text(
                        text = spot,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (!elapsedTime.isNullOrBlank()) {
            Spacer(modifier = Modifier.size(spacing.SpaceXs))
            Text(
                text = elapsedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                maxLines = 2,
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
