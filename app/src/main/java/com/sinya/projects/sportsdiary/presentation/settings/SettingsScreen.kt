package com.sinya.projects.sportsdiary.presentation.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.core.domain.model.SwitchItem
import com.sinya.projects.sportsdiary.core.utils.updateLocale
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.settings.modalSheetLocale.SettingsLanguageSheet
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.ListCardItem
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import java.util.Locale

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is SettingsUiState.Error -> ErrorScreen((state as SettingsUiState.Error).message)

        SettingsUiState.Loading -> PlaceholderScreen()

        is SettingsUiState.Success -> SettingsScreenView(
            onBackClick = onBackClick,
            state = state as SettingsUiState.Success,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
private fun SettingsScreenView(
    onBackClick: () -> Unit,
    state: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit
) {
    val context = LocalContext.current
    val stateBottomSheet = remember { mutableStateOf(false) }

    ScreenColumn(
        navigationType = TypeAppTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = stringResource(R.string.settings_title)
        )
    ) {
        BlockOfCards(
            title = stringResource(R.string.interface_block)
        ) {
            ListCardItem(
                onClick = { stateBottomSheet.value = !stateBottomSheet.value },
                title = stringResource(R.string.language_title),
                description = Locale(state.langMode).displayName.lowercase(),
            )
            ListCardItem(
                onClick = { },
                title = stringResource(R.string.theme_title),
                description = if (state.themeMode) stringResource(R.string.dark_mode)
                else stringResource(R.string.light_mode),
                state = SwitchItem(
                    state = state.themeMode,
                    onClick = { onEvent(SettingsEvent.ThemeToggle(!state.themeMode)) }
                )
            )
            ListCardItem(
                onClick = { },
                title = stringResource(R.string.warning_dialog_title),
                state = SwitchItem(
                    state = state.showTrainingWarningState,
                    onClick = { onEvent(SettingsEvent.ShowTrainingWarningToggle(!state.showTrainingWarningState)) }
                )
            )
            Spacer(Modifier.height(80.dp))
        }
    }

    if (stateBottomSheet.value) {
        SettingsLanguageSheet(
            setLanguage = { lang ->
                onEvent(SettingsEvent.LanguageToggle(lang))
                context.updateLocale(lang)
            },
            language = state.langMode,
            onDismiss = {
                stateBottomSheet.value = !stateBottomSheet.value
            }
        )
    }
}