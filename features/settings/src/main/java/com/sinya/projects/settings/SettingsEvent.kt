package com.sinya.projects.settings

import kotlinx.coroutines.CoroutineScope

internal sealed interface SettingsEvent {
    data class ThemeToggle(val state: Boolean) : SettingsEvent
    data class LanguageToggle(val lang: String) : SettingsEvent
    data class ShowTrainingWarningToggle(val state: Boolean) : SettingsEvent
    class MapStateToggle(val map7: Boolean) : SettingsEvent
    data object SelectChangelog : SettingsEvent
}
