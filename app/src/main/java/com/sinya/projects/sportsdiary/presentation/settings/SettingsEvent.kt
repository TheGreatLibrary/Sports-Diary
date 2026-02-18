package com.sinya.projects.sportsdiary.presentation.settings

sealed interface SettingsEvent {
    data class ThemeToggle(val state: Boolean) : SettingsEvent
    data class LanguageToggle(val lang: String) : SettingsEvent
    data class ShowTrainingWarningToggle(val state: Boolean) : SettingsEvent
}
