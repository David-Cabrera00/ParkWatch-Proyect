package com.david.parkwatch.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.david.parkwatch.presentation.component.ParkPrimaryButton
import com.david.parkwatch.R
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSaveVehicleClick: () -> Unit,
) {
    val colors = parkColors()
    val spacing = parkSpacing()

    ScreenScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(spacing.SpaceLg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.home_empty_title),
                style = androidx.wear.compose.material3.MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(spacing.SpaceXl))
            ParkPrimaryButton(
                onClick = onSaveVehicleClick,
                label = stringResource(R.string.home_save_vehicle),
            )
        }
    }
}