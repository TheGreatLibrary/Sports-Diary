package com.sinya.projects.form

import com.sinya.projects.domain.enums.Gender
import java.time.LocalDate

internal sealed interface UserProfileUiState {
    object Loading : UserProfileUiState
    data class Success(
        val birthDateMillis: LocalDate = LocalDate.of(2008, 1, 1),
        val gender: Gender = Gender.MALE,
        val targetWeightKg: Float = 75.0f,
        val dailyStepsGoal: Int = 10000,
        val sleepDurationMinutes: Int = 480, // 8 часов
        val dailyCaloriesGoal: Int = 2500,
        val maxHeartRate: Int = 195,
        val autoCalculateMaxHr: Boolean = true
    ) : UserProfileUiState
}