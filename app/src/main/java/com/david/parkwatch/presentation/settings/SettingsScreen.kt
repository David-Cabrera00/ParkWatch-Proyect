package com.david.parkwatch.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.RadioButton
import androidx.wear.compose.material3.RadioButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.david.parkwatch.R
import com.david.parkwatch.data.preferences.AppPreferences
import com.david.parkwatch.data.preferences.ParkLanguage
import com.david.parkwatch.data.preferences.ParkThemeMode
import com.david.parkwatch.presentation.component.parkRotaryScroll
import com.david.parkwatch.presentation.theme.parkColors
import com.david.parkwatch.presentation.theme.parkSpacing

@Composable
fun SettingsScreen(
    preferences: AppPreferences,
    onThemeSelected: (ParkThemeMode) -> Unit,
    onLanguageSelected: (ParkLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = parkColors()
    val spacing = parkSpacing()
    val listState = rememberScalingLazyListState()

    ScreenScaffold(modifier = modifier.fillMaxSize().background(colors.background)) { contentPadding ->
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .parkRotaryScroll(listState),
            contentPadding = PaddingValues(vertical = spacing.SpaceLg, horizontal = spacing.SpaceMd),
            verticalArrangement = Arrangement.spacedBy(spacing.SpaceSm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onBackground,
                )
            }
            item {
                Spacer(modifier = Modifier.size(spacing.SpaceXs))
                Text(
                    text = stringResource(R.string.settings_theme_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant,
                )
            }
            item {
                SettingsOption(
                    label = stringResource(R.string.settings_theme_system),
                    selected = preferences.themeMode == ParkThemeMode.SYSTEM,
                    onSelect = { onThemeSelected(ParkThemeMode.SYSTEM) },
                )
            }
            item {
                SettingsOption(
                    label = stringResource(R.string.settings_theme_light),
                    selected = preferences.themeMode == ParkThemeMode.LIGHT,
                    onSelect = { onThemeSelected(ParkThemeMode.LIGHT) },
                )
            }
            item {
                SettingsOption(
                    label = stringResource(R.string.settings_theme_dark),
                    selected = preferences.themeMode == ParkThemeMode.DARK,
                    onSelect = { onThemeSelected(ParkThemeMode.DARK) },
                )
            }
            item {
                Spacer(modifier = Modifier.size(spacing.SpaceSm))
                Text(
                    text = stringResource(R.string.settings_language_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant,
                )
            }
            item {
                SettingsOption(
                    label = stringResource(R.string.settings_language_system),
                    selected = preferences.language == ParkLanguage.SYSTEM,
                    onSelect = { onLanguageSelected(ParkLanguage.SYSTEM) },
                )
            }
            item {
                SettingsOption(
                    label = stringResource(R.string.settings_language_spanish),
                    selected = preferences.language == ParkLanguage.SPANISH,
                    onSelect = { onLanguageSelected(ParkLanguage.SPANISH) },
                )
            }
            item {
                SettingsOption(
                    label = stringResource(R.string.settings_language_english),
                    selected = preferences.language == ParkLanguage.ENGLISH,
                    onSelect = { onLanguageSelected(ParkLanguage.ENGLISH) },
                )
            }
        }
    }
}

@Composable
private fun SettingsOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val colors = parkColors()
    RadioButton(
        selected = selected,
        onSelect = onSelect,
        colors = RadioButtonDefaults.radioButtonColors(
            selectedContainerColor = colors.primaryContainer,
            selectedContentColor = colors.onPrimaryContainer,
            selectedControlColor = colors.primary,
            unselectedContainerColor = colors.surfaceContainer,
            unselectedContentColor = colors.onSurface,
            unselectedControlColor = colors.onSurfaceVariant,
        ),
        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
    )
}
