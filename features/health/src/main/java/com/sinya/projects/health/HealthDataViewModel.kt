package com.sinya.projects.health

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.managers.HealthManager
import com.sinya.projects.domain.managers.HealthSdkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HealthDataViewModel @Inject constructor(
    private val healthManager: HealthManager
) : ViewModel() {

    private val _state = MutableStateFlow(HealthDashboardState())
    val state: StateFlow<HealthDashboardState> = _state.asStateFlow()

    val permissions: Set<String> get() = healthManager.permissions

    fun getPermissionContract(): ActivityResultContract<Set<String>, Set<String>> {
        return healthManager.getPermissionContract()
    }

    fun getPermissionIntent(): Intent = healthManager.getPermissionIntent()

    init {
        checkSdkStatus()
    }

    private fun checkSdkStatus() {
        viewModelScope.launch {
            val status = healthManager.getSdkStatus()
            _state.update { it.copy(sdkStatus = status) }
            if (status == HealthSdkStatus.AVAILABLE) {
                loadHealthData()
            }
        }
    }

    fun loadHealthData() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val data = healthManager.fetchHealthData()
                _state.update {
                    it.copy(
                        isLoading = false,
                        isConnected = true,
                        stepsToday = data.stepsToday,
                        activeKcalToday = data.activeKcalToday,
                        totalKcalToday = data.totalKcalToday,
                        exerciseMinutesToday = data.exerciseMinutesToday,
                        activeHoursToday = data.activeHoursToday,
                        latestExerciseDate = data.latestExerciseDate,
                        distanceKmToday = data.distanceKmToday,
                        floorsClimbedToday = data.floorsClimbedToday,
                        elevationGainMeters = data.elevationGainMeters,
                        latestHeartRate = data.latestHeartRate,
                        restingHeartRate = data.restingHeartRate,
                        heartRateHourly = data.heartRateHourly,
                        latestSpo2 = data.latestSpo2,
                        spo2Hourly = data.spo2Hourly,
                        hrvRmssd = data.hrvRmssd,
                        stressScore = data.stressScore,
                        stressHourly = data.stressHourly,
                        sleepDurationMinutes = data.sleepDurationMinutes,
                        respiratoryRate = data.respiratoryRate,
                        vo2Max = data.vo2Max,
                        hydrationLitersToday = data.hydrationLitersToday,
                        weightKg = data.weightKg,
                        bodyFatPercent = data.bodyFatPercent,
                        leanBodyMassKg = data.leanBodyMassKg,
                        boneMassKg = data.boneMassKg,
                        bodyWaterKg = data.bodyWaterKg,
                        bmrKcal = data.bmrKcal
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, statusMessage = e.message) }
            }
        }
    }
}
