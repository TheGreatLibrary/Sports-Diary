package com.sinya.projects.sportsdiary.presentation.settings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(
        val themeMode: Boolean,
        val langMode: String,
        val showTrainingWarningState: Boolean
    ) : SettingsUiState

    data class Error(val message: String) : SettingsUiState
}