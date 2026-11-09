package com.sinya.projects.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.managers.SettingsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UserProfileViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine
) : ViewModel() {

    private val _state = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val state: StateFlow<UserProfileUiState> = _state.asStateFlow()

    init {
        _state.value = UserProfileUiState.Success()

        viewModelScope.launch {
            val initialConfig = settingsEngine.uiState.first()
            _state.update {
                UserProfileUiState.Success(
                    birthDateMillis = initialConfig.birthDateMillis,
                    gender = initialConfig.gender,
                    targetWeightKg = initialConfig.targetWeightKg,
                    dailyStepsGoal = initialConfig.dailyStepsGoal,
                    sleepDurationMinutes = initialConfig.sleepDurationMinutes,
                    dailyCaloriesGoal = initialConfig.targetActiveKcal,
                    maxHeartRate = initialConfig.maxHeartRate,
                    autoCalculateMaxHr = initialConfig.autoCalculateMaxHr
                )
            }

            settingsEngine.uiState.collect { config ->
                updateIfSuccess { currentState ->
                    currentState.copy(
                        birthDateMillis = config.birthDateMillis,
                        gender = config.gender,
                        targetWeightKg = config.targetWeightKg,
                        dailyStepsGoal = config.dailyStepsGoal,
                        sleepDurationMinutes = config.sleepDurationMinutes,
                        dailyCaloriesGoal = config.targetActiveKcal,
                        maxHeartRate = config.maxHeartRate,
                        autoCalculateMaxHr = config.autoCalculateMaxHr
                    )
                }
            }
        }
    }

    fun onEvent(event: UserProfileEvent) {
        when (event) {
            is UserProfileEvent.OnGenderSelected -> settingsEngine.setGender(event.gender)

            is UserProfileEvent.OnDateSelected -> settingsEngine.setBirthDateMillis(event.date.toLocalDate())

            is UserProfileEvent.OnWeightSelected -> settingsEngine.setTargetWeight(event.weight)

            is UserProfileEvent.OnStepsSelected -> settingsEngine.setTargetStepsCount(event.steps)

            is UserProfileEvent.OnSleepSelected -> settingsEngine.setTargetSleepCount(event.minutes)

            is UserProfileEvent.OnCaloriesSelected -> settingsEngine.targetKcalCount(event.it)

            is UserProfileEvent.OnHeartRateSelected -> {
                settingsEngine.setMaxPlc(event.manual)
                settingsEngine.setAutoCalculateMaxHr(event.auto)
            }
        }
    }

    fun updateIfSuccess(transform: (UserProfileUiState.Success) -> UserProfileUiState.Success) {
        _state.update { currentState ->
            if (currentState is UserProfileUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}