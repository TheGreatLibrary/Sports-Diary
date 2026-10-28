package com.sinya.projects.datastore.providers

import com.sinya.projects.datastore.SettingsEngine
import com.sinya.projects.domain.providers.LanguageProvider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
class LanguageProviderImpl @Inject constructor(
    private val settingsEngine: SettingsEngine
) : LanguageProvider {

    override val currentLanguage: Flow<String> = settingsEngine.uiState
        .map { it.language }
        .distinctUntilChanged()
}

