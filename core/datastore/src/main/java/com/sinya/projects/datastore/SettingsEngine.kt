package com.sinya.projects.datastore

import com.sinya.projects.domain.model.UiConfig
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Singleton
class SettingsEngine @Inject constructor(
    private val store: DataStoreManager
) {
    private val _state = AtomicState(UiConfig())
    val uiState: StateFlow<UiConfig> = _state.state

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun hydrateCritical() {
        val prefs = store.readAllCriticalPreferences()
        _state.update { current ->
            current.copy(
                dark = prefs.dark,
                map7 = prefs.map7,
                language = prefs.language,
                trainingWarning = prefs.trainingWarning,
                planMorningId = prefs.planMorningId
            )
        }
    }

    fun setDark(v: Boolean) {
        _state.update { it.copy(dark = v) }
        persist { store.setThemeMode(v) }
    }

    fun setMap7(v: Boolean) {
        _state.update { it.copy(map7 = v) }
        persist { store.setMap7Mode(v) }
    }

    fun setLang(v: String) {
        _state.update { it.copy(language = v) }
        persist { store.setLangMode(v) }
    }

    fun setTrainingWarning(v: Boolean) {
        _state.update { it.copy(trainingWarning = v) }
        persist { store.setShowTrainingWarningState(v) }
    }

    fun setPlanMorningId(v: Int?) {
        _state.update { it.copy(planMorningId = v) }
        persist { store.setPlanMorningId(v) }
    }

    private fun persist(block: suspend () -> Unit) {
        scope.launch { runCatching { block() } }
    }
}

