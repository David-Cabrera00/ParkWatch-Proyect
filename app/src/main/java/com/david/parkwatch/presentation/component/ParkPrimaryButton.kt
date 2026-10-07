package com.david.parkwatch.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun ParkPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = colors.primary,
        contentColor = colors.onPrimary,
        disabledContainerColor = colors.primary.copy(alpha = 0.38f),
        disabledContentColor = colors.onPrimary.copy(alpha = 0.38f),
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.SpaceLg),
        colors = buttonColors,
        enabled = enabled,
    ) {
        if (icon != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                icon()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(spacing.SpaceSm))
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                )
            }
        } else {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            )
        }
    }
}