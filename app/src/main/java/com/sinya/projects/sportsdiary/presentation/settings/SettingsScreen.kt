package com.sinya.projects.sportsdiary.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.settings.modalSheetLocale.SettingsLanguageSheet
import com.sinya.projects.sportsdiary.ui.features.AnimationCard
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.ColumnOfCard
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
        themeCurrent =  if (themeMode) stringResource(R.string.dark_mode)
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
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            type = TypeAppTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.settings_title)
            )
        )

//        BlockOfCards(
//            title = stringResource(R.string.main_block)
//        ) {
//            CardMenu(
//                title = stringResource(R.string.training_title),
//                description = stringResource(R.string.list_of_training),
//                onClick = onTrainingClick
//            )
//
//        }
        BlockOfCards(
            title = stringResource(R.string.interface_block)
        ) {
            AnimationCard(
                onClick = onLanguageClick
            ) {
                ColumnOfCard(
                    title = stringResource(R.string.language_title),
                    description = Locale(language).displayName.lowercase(),
                )
            }
            AnimationCard(
                onClick = toggleTheme
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.94f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColumnOfCard(
                        title = stringResource(R.string.theme_title),
                        description = themeCurrent,
                    )
                    Switch(
                        checked = themeMode,
                        onCheckedChange = { toggleTheme() },
                        Modifier
                            .size(28.dp, 20.dp)
                            .scale(0.73f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary, // Цвет кружка в выключенном состоянии
                            checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer, // Цвет трека в включенном состоянии
                            uncheckedTrackColor = MaterialTheme.colorScheme.background, // Цвет трека в выключенном состоянии
                            uncheckedBorderColor = Color.Transparent
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
}