package com.sinya.projects.sportsdiary.presentation.home

import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import com.sinya.projects.sportsdiary.domain.model.Training
import java.time.LocalDate

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val date: LocalDate,
        val monthDays: List<DayOfMonth>,
        val trainingList: List<Training> = emptyList(),
        val calendarExpanded: Boolean = false,
        val errorMessage: String? = null
    ) : HomeUiState
}

