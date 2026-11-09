package com.sinya.projects.domain.model

import com.sinya.projects.domain.enums.Gender
import com.sinya.projects.domain.enums.TypeLanguages
import java.time.LocalDate

data class UiConfig(
    val dark: Boolean = true,
    val map7: Boolean = true,
    val language: String = TypeLanguages.RU.code,
    val trainingWarning: Boolean = true,
    val planMorningId: Int? = null,

    val gender: Gender = Gender.MALE,
    val birthDateMillis: LocalDate = LocalDate.of(2008, 1, 1),
    val maxHeartRate: Int = 0,
    val autoCalculateMaxHr: Boolean = true,
    val targetWeightKg: Float = 75f,
    val dailyStepsGoal: Int = 10000,
    val sleepDurationMinutes: Int = 480,
    val targetActiveKcal: Int = 400
)