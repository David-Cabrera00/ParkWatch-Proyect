package com.david.parkwatch.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun ParkDestructiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = colors.error,
        contentColor = colors.onError,
        disabledContainerColor = colors.error.copy(alpha = 0.38f),
        disabledContentColor = colors.onError.copy(alpha = 0.38f),
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = spacing.SpaceMd),
        colors = buttonColors,
        enabled = enabled,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = colors.onError,
            )
        },
    )
}

