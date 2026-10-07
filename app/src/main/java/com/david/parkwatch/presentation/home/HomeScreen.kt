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
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ScreenScaffold
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material3.Text
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
    val listState = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = listState,
        modifier = modifier.fillMaxSize(),
    ) { contentPadding ->
        TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing.SpaceLg)
                        .background(colors.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.home_empty_title),
                        fontSize = 22.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
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
    }
}