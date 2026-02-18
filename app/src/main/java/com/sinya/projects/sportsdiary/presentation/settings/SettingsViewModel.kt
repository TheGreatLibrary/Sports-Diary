package com.sinya.projects.sportsdiary.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _state = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        combine(
            dataStoreManager.getLangMode(),
            dataStoreManager.getThemeMode(),
            dataStoreManager.getShowTrainingWarningState()
        ) { langMode, themeMode, showTrainingWarningState ->
            SettingsUiState.Success(
                langMode = langMode,
                themeMode = themeMode,
                showTrainingWarningState = showTrainingWarningState
            )
        }
        .collect { newState ->
            _state.value = newState
        }
    }

    fun onEvent(event: SettingsEvent) = viewModelScope.launch {
        when(event) {
            is SettingsEvent.LanguageToggle -> dataStoreManager.setLangMode(event.lang)

            is SettingsEvent.ShowTrainingWarningToggle -> dataStoreManager.setShowTrainingWarningState(event.state)

            is SettingsEvent.ThemeToggle -> dataStoreManager.setThemeMode(event.state)
        }
    }
}