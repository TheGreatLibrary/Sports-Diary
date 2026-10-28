package com.sinya.projects.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.datastore.SettingsEngine
import com.sinya.projects.domain.model.ChangelogState
import com.sinya.projects.domain.useCase.GetChangelogListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine,
    private val getChangelogListUseCase: GetChangelogListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()
    private val _changelogState = MutableStateFlow<ChangelogState>(ChangelogState.Loading)
    val changelogState: StateFlow<ChangelogState> = _changelogState.asStateFlow()

    init {
        viewModelScope.launch {
            val initialConfig = settingsEngine.uiState.first()
            _state.update {
                SettingsUiState.Success(
                    themeMode = initialConfig.dark,
                    mapState = initialConfig.map7,
                    langMode = initialConfig.language,
                    showTrainingWarningState = initialConfig.trainingWarning
                )
            }

            fetchChangelog()

            settingsEngine.uiState.collect { config ->
                updateIfSuccess { currentState ->
                    currentState.copy(
                        themeMode = config.dark,
                        mapState = config.map7,
                        langMode = config.language,
                        showTrainingWarningState = config.trainingWarning
                    )
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) = viewModelScope.launch {
        when (event) {
            is SettingsEvent.LanguageToggle -> settingsEngine.setLang(event.lang)
            is SettingsEvent.MapStateToggle -> settingsEngine.setMap7(event.map7)
            is SettingsEvent.ShowTrainingWarningToggle -> settingsEngine.setTrainingWarning(event.state)
            is SettingsEvent.ThemeToggle -> settingsEngine.setDark(event.state)
            is SettingsEvent.SelectChangelog -> fetchChangelog() // Перезапуск загрузки
        }
    }

    private fun fetchChangelog() = viewModelScope.launch {
        _changelogState.value = ChangelogState.Loading

        getChangelogListUseCase().fold(
            onSuccess = { data ->
                _changelogState.value = ChangelogState.Success(data)
            },
            onFailure = { error ->
                _changelogState.value = ChangelogState.Error(error.toString())
            }
        )
    }

    private fun updateIfSuccess(transform: (SettingsUiState.Success) -> SettingsUiState.Success) {
        _state.update { currentState ->
            if (currentState is SettingsUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}