package com.sinya.projects.sportsdiary.presentation.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.ChangelogState
import com.sinya.projects.sportsdiary.core.domain.model.SwitchItem
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.core.utils.LogcatReader
import com.sinya.projects.sportsdiary.core.utils.UrlConfig
import com.sinya.projects.sportsdiary.core.utils.openUrl
import com.sinya.projects.sportsdiary.core.utils.sendSupportMail
import com.sinya.projects.sportsdiary.core.utils.updateLocale
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.settings.composable.AppVersionInfo
import com.sinya.projects.sportsdiary.presentation.settings.composable.SettingsPlaceholder
import com.sinya.projects.sportsdiary.sheets.ChangesSheetContent
import com.sinya.projects.sportsdiary.sheets.SettingsLanguageSheet
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.ListCardItem
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val changelogState by viewModel.changelogState.collectAsStateWithLifecycle()
    when (state) {
        is SettingsUiState.Error -> ErrorScreen(
            onBackClick,
            (state as SettingsUiState.Error).message
        )

        SettingsUiState.Loading -> SettingsPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(R.string.settings_title)
        )

        is SettingsUiState.Success -> {
            SettingsScreenView(
                onBackClick = onBackClick,
                changelogState = changelogState,
                state = state as SettingsUiState.Success,
                onEvent = viewModel::onEvent,
                sendEmail = {
                    context.sendSupportMail()
                },
                copyErrors = {
                    scope.launch {
                        val systemLogs = LogcatReader.getAppLogs(context)
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("App Logcat", systemLogs)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Логи скопированы в буфер!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }
    }
}


@Composable
private fun SettingsScreenView(
    onBackClick: () -> Unit,
    state: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit,
    sendEmail: () -> Unit,
    copyErrors: () -> Unit,
    changelogState: ChangelogState,
) {
    val context = LocalContext.current
    val stateBottomSheet = remember { mutableStateOf(false) }
    val changeStateSheet = remember { mutableStateOf(false) }

    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
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
                description = Locale(state.langMode).displayName.replaceFirstChar { it.uppercaseChar() }
            )
            ListCardItem(
                onClick = { onEvent(SettingsEvent.ThemeToggle(!state.themeMode)) },
                title = stringResource(R.string.theme_title),
                description = when (state.themeMode) {
                    true -> stringResource(R.string.dark_mode)
                    false -> stringResource(R.string.light_mode)
                },
                state = SwitchItem(
                    state = state.themeMode,
                    onClick = { onEvent(SettingsEvent.ThemeToggle(!state.themeMode)) }
                )
            )
        }
        BlockOfCards(
            title = stringResource(R.string.app_block)
        ) {
            ListCardItem(
                onClick = copyErrors,
                title = stringResource(R.string.copy_errors),
                description = stringResource(R.string.copy_errors_description)
            )
            ListCardItem(
                onClick = sendEmail,
                title = stringResource(R.string.send_to_support),
                description = stringResource(R.string.send_to_support_description)
            )
            ListCardItem(
                onClick = { changeStateSheet.value = !changeStateSheet.value },
                title = stringResource(R.string.list_of_changes),
                description = stringResource(R.string.see_what_changes_in_the_update)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                AppVersionInfo(state.langMode)
            }
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

    if (changeStateSheet.value) {
        ChangesSheetContent(
            onDismiss = { changeStateSheet.value = !changeStateSheet.value },
            changeLogState = changelogState,
            onReload = { onEvent(SettingsEvent.SelectChangelog) },
            upToMarket = { context.openUrl(UrlConfig.marketRuStore) }
        )
    }
}