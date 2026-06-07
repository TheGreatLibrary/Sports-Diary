package com.sinya.projects.sportsdiary.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = settingsEngine.uiState
        .map { config ->
            SettingsUiState.Success(
                langMode = config.language,
                themeMode = config.dark,
                showTrainingWarningState = config.trainingWarning
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SettingsUiState.Loading
        )

    fun onEvent(event: SettingsEvent) = viewModelScope.launch {
        when (event) {
            is SettingsEvent.LanguageToggle -> settingsEngine.setLang(event.lang)
            is SettingsEvent.ShowTrainingWarningToggle -> settingsEngine.setTrainingWarning(event.state)
            is SettingsEvent.ThemeToggle -> settingsEngine.setDark(event.state)
        }
    }
}