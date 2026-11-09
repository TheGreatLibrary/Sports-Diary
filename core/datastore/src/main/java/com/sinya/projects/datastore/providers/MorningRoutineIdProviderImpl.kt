package com.sinya.projects.datastore.providers

import com.sinya.projects.domain.managers.SettingsEngine
import com.sinya.projects.domain.providers.MorningRoutineIdProvider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
internal class MorningRoutineIdProviderImpl @Inject constructor(
    private val settingsEngine: SettingsEngine
) : MorningRoutineIdProvider {

    override val morningRoutineId: Flow<Int?> = settingsEngine.uiState
        .map { it.planMorningId }
        .distinctUntilChanged()
}