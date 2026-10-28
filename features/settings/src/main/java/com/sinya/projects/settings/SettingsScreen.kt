package com.sinya.projects.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.sinya.projects.domain.model.CardItem
import com.sinya.projects.domain.model.ChangelogState
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.domain.utils.LogcatReader
import com.sinya.projects.domain.utils.UrlConfig
import com.sinya.projects.domain.utils.openUrl
import com.sinya.projects.domain.utils.sendSupportMail
import com.sinya.projects.domain.utils.updateLocale
import com.sinya.projects.settings.composable.AppVersionInfo
import com.sinya.projects.settings.composable.ChangesSheetContent
import com.sinya.projects.settings.composable.SettingsLanguageSheet
import com.sinya.projects.settings.composable.SettingsPlaceholder
import com.sinya.projects.ui.components.BlockOfCards
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.screen.ScreenColumn
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current

    val viewModel: SettingsViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val changelogState by viewModel.changelogState.collectAsStateWithLifecycle()
    when (state) {
        is SettingsUiState.Error -> {}

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
                        val systemLogs = LogcatReader.getAppLogs(context   )
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
        ),
        arrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BlockOfCards(
                title = stringResource(R.string.settings_interface),
                list = listOf(
                    CardItem(
                        title = stringResource(R.string.settings_lang),
                        description = Locale(state.langMode).displayName.replaceFirstChar { it.uppercaseChar() },
                        icon = R.drawable.set_lang,
                        onClick = { stateBottomSheet.value = !stateBottomSheet.value }
                    ),
                    CardItem(
                        title = stringResource(R.string.settings_theme),
                        description = when (state.themeMode) {
                            true -> stringResource(R.string.settings_dark_mode)
                            false -> stringResource(R.string.settings_light_mode)
                        },
                        icon = R.drawable.set_theme_night,
                        onClick = { onEvent(SettingsEvent.ThemeToggle(!state.themeMode)) },
                        switchState = state.themeMode
                    ),
                    CardItem(
                        title = stringResource(R.string.heat_map),
                        description = when (state.mapState) {
                            true -> stringResource(R.string.day_7)
                            false -> stringResource(R.string.week)
                        },
                        icon = com.sinya.projects.ui.R.drawable.train_muscul,
                        onClick = { onEvent(SettingsEvent.MapStateToggle(!state.mapState)) },
                        switchState = state.mapState
                    )
                )
            )
            BlockOfCards(
                title = stringResource(R.string.settings_app),
                list = listOf(
                    CardItem(
                        title = stringResource(R.string.settings_copy_errors),
                        icon = R.drawable.set_logs,
                        onClick = copyErrors
                    ),
                    CardItem(
                        title = stringResource(R.string.settings_send_to_support),
                        icon = R.drawable.set_support,
                        onClick = sendEmail
                    ),
                    CardItem(
                        title = stringResource(R.string.settings_list_of_changes),
                        icon = R.drawable.set_changes,
                        onClick = { changeStateSheet.value = !changeStateSheet.value }
                    )
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            AppVersionInfo(state.langMode)
        }
        Spacer(Modifier.height(80.dp))
    }

    if (stateBottomSheet.value || changeStateSheet.value) {
        CustomModalSheet(
            modifier = if (changeStateSheet.value) Modifier else Modifier.verticalScroll(rememberScrollState()),
            onDismiss = {
                when {
                    stateBottomSheet.value -> {
                        stateBottomSheet.value = !stateBottomSheet.value
                    }
                    else -> {
                        changeStateSheet.value = !changeStateSheet.value
                    }
                }
            }
        ) {
            when {
                stateBottomSheet.value -> {
                    SettingsLanguageSheet(
                        setLanguage = { lang ->
                            onEvent(SettingsEvent.LanguageToggle(lang))
                            context.updateLocale(lang)
                        },
                        language = state.langMode,
                    )
                }
                changeStateSheet.value -> {
                    ChangesSheetContent(
                        changeLogState = changelogState,
                        onReload = { onEvent(SettingsEvent.SelectChangelog) },
                        upToMarket = { context.openUrl(UrlConfig.marketRuStore) }
                    )
                }
            }
        }
    }
}