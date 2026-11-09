package com.sinya.projects.datastore

import com.sinya.projects.domain.enums.Gender
import com.sinya.projects.domain.managers.SettingsEngine
import com.sinya.projects.domain.model.UiConfig
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.TimeZone

@Singleton
internal class SettingsEngineImpl @Inject constructor(
    private val store: DataStoreManager
) : SettingsEngine {
    private val _state = AtomicState(UiConfig())
    override val uiState: StateFlow<UiConfig> = _state.state

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun hydrateCritical() {
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
        hydrateOptional()
    }

    private fun hydrateOptional() {
        scope.launch {
            val prefs = store.readAllOptionalPreferences()
            _state.update { current ->
                current.copy(
                    gender = prefs.gender,
                    birthDateMillis = prefs.birthDateMillis,
                    targetWeightKg = prefs.targetWeight,
                    dailyStepsGoal = prefs.targetStepAtDay,
                    sleepDurationMinutes = prefs.targetSleepAtDay,
                    targetActiveKcal = prefs.targetCaloriesAtDay,
                    autoCalculateMaxHr = prefs.autoCalculateMaxHr,
                    maxHeartRate = prefs.maxHeartRate
                )
            }
        }
    }

    override fun setDark(v: Boolean) {
        _state.update { it.copy(dark = v) }
        persist { store.setThemeMode(v) }
    }

    override fun setMap7(v: Boolean) {
        _state.update { it.copy(map7 = v) }
        persist { store.setMap7Mode(v) }
    }

    override fun setLang(v: String) {
        _state.update { it.copy(language = v) }
        persist { store.setLangMode(v) }
    }

    override fun setTrainingWarning(v: Boolean) {
        _state.update { it.copy(trainingWarning = v) }
        persist { store.setShowTrainingWarningState(v) }
    }

    override fun setPlanMorningId(v: Int?) {
        _state.update { it.copy(planMorningId = v) }
        persist { store.setPlanMorningId(v) }
    }

    override fun setGender(v: Gender) {
        _state.update { it.copy(gender = v) }
        persist { store.setGender(v.ordinal) }
    }

    override fun setBirthDateMillis(v: LocalDate) {
        _state.update { it.copy(birthDateMillis = v) }
        persist {
            val millis = v.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
            store.setBirthDateMillis(millis)
        }
    }

    override fun setMaxPlc(v: Int) {
        _state.update { it.copy(maxHeartRate = v) }
        persist { store.setMaxHeartRate(v) }
    }

    override fun setTargetWeight(v: Float) {
        _state.update { it.copy(targetWeightKg = v) }
        persist { store.setTargetWeight(v) }
    }

    override fun setTargetStepsCount(v: Int) {
        _state.update { it.copy(dailyStepsGoal = v) }
        persist { store.setTargetStepsCount(v) }
    }

    override fun setTargetSleepCount(v: Int) {
        _state.update { it.copy(sleepDurationMinutes = v) }
        persist { store.setTargetSleepCount(v) }
    }

    override fun setAutoCalculateMaxHr(v: Boolean) {
        _state.update { it.copy(autoCalculateMaxHr = v) }
        persist { store.setAutoCalculateMaxHr(v) }
    }

    override fun targetKcalCount(v: Int) {
        _state.update { it.copy(targetActiveKcal = v) }
        persist { store.setTargetKcalCount(v) }
    }

    private fun persist(block: suspend () -> Unit) {
        scope.launch { runCatching { block() } }
    }
}

