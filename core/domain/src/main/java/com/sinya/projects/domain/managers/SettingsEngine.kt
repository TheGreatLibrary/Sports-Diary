package com.sinya.projects.domain.managers

import com.sinya.projects.domain.enums.Gender
import com.sinya.projects.domain.model.UiConfig
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface SettingsEngine {
    val uiState: StateFlow<UiConfig>

    suspend fun hydrateCritical()

    fun setDark(v: Boolean)

    fun setMap7(v: Boolean)

    fun setLang(v: String)

    fun setTrainingWarning(v: Boolean)

    fun setPlanMorningId(v: Int?)

    fun setGender(v: Gender)

    fun setBirthDateMillis(v: LocalDate)

    fun setMaxPlc(v: Int)

    fun setAutoCalculateMaxHr(v: Boolean)

    fun setTargetWeight(v: Float)

    fun setTargetStepsCount(v: Int)

    fun setTargetSleepCount(v: Int)

    fun targetKcalCount(v: Int)
}