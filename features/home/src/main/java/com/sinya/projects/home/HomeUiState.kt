package com.sinya.projects.home

import com.sinya.projects.domain.model.DayOfMonth
import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.domain.model.Training

internal sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val date: DayOfMonth,
        val currentPlanId: Int?,
        val monthDays: List<DayOfMonth>,
        val trainingList: List<Training> = emptyList(),
        val calendarExpanded: Boolean = false,
        val errorMessage: String? = null,
        val stepsToday: Long = 0L,
        val sleepDurationMinutes: Long = 0L,
        val totalKcalToday: Int = 0,
        val proportionList: List<ProportionsEntry> = emptyList()
    ) : HomeUiState
}

