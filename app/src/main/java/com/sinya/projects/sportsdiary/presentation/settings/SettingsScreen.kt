package com.sinya.projects.sportsdiary.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.SwitchItem
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.settings.modalSheetLocale.SettingsLanguageSheet
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.ListCardItem
import java.util.Locale

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    toggleTheme: () -> Unit,
    setLanguage: (String) -> Unit,
    themeMode: Boolean,
    language: String
) {
    val stateBottomSheet = remember { mutableStateOf(false) }

    SettingsScreenView(
        stateBottomSheet = stateBottomSheet.value,
        onLanguageClick = { stateBottomSheet.value = !stateBottomSheet.value },
        onBackClick = onBackClick,
        themeCurrent = if (themeMode) stringResource(R.string.dark_mode)
                        else stringResource(R.string.light_mode),
        toggleTheme = toggleTheme,
        setLanguage = setLanguage,
        language = language,
        themeMode = themeMode
    )
}

@Composable
private fun SettingsScreenView(
    stateBottomSheet: Boolean,
    onLanguageClick: () -> Unit,
    onBackClick: () -> Unit,
    themeCurrent: String,
    toggleTheme: () -> Unit,
    setLanguage: (String) -> Unit,
    themeMode: Boolean,
    language: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 50.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        NavigationTopBar(
            type = TypeAppTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.settings_title)
            )
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BlockOfCards(
                title = stringResource(R.string.interface_block)
            ) {
                ListCardItem(
                    onClick = onLanguageClick,
                    title = stringResource(R.string.language_title),
                    description = Locale(language).displayName.lowercase(),
                )
                ListCardItem(
                    onClick = toggleTheme,
                    title = stringResource(R.string.theme_title),
                    description = themeCurrent,
                    state = SwitchItem(
                        state = themeMode,
                        onClick = { toggleTheme() }
                    )
                )
            }
        }
    }

    if (stateBottomSheet) {
        SettingsLanguageSheet(
            setLanguage = setLanguage,
            language = language,
            onDismiss = onLanguageClick
        )
    }
}