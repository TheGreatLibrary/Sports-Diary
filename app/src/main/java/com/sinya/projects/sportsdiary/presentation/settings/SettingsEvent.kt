package com.sinya.projects.sportsdiary.presentation.settings

import kotlinx.coroutines.CoroutineScope

sealed interface SettingsEvent {
    data class ThemeToggle(val state: Boolean) : SettingsEvent
    data class LanguageToggle(val lang: String) : SettingsEvent
    data class ShowTrainingWarningToggle(val state: Boolean) : SettingsEvent
    data object SelectChangelog : SettingsEvent
}
