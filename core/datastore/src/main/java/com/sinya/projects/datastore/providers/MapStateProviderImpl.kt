package com.sinya.projects.datastore.providers

import com.sinya.projects.datastore.SettingsEngine
import com.sinya.projects.domain.providers.MapStateProvider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
class MapStateProviderImpl @Inject constructor(
    private val settingsEngine: SettingsEngine
) : MapStateProvider {

    override val currentMapState: Flow<Boolean> = settingsEngine.uiState
        .map { it.map7 }
        .distinctUntilChanged()
}