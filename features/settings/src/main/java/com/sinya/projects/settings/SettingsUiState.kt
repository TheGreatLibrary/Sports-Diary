package com.sinya.projects.settings

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(
        val themeMode: Boolean,
        val langMode: String,
        val showTrainingWarningState: Boolean,
        val mapState: Boolean,
    ) : SettingsUiState

    data class Error(val message: String) : SettingsUiState
}