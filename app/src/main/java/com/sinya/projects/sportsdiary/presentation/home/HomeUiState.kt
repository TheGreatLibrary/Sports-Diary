package com.sinya.projects.sportsdiary.presentation.home

import com.sinya.projects.sportsdiary.presentation.trainings.Training
import java.time.YearMonth

sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data class Success(
        val year: Int,
        val month: Int,
        val monthDays: List<DayOfMonth>,
        val trainingList: List<Training> = emptyList(),
        val morningState: Boolean = false,
        val trainingState: Boolean = false,
        val calendarExpanded: Boolean = false,
        val currentMonth: YearMonth = YearMonth.now()
    ) : HomeScreenUiState()
    data class Error(val message: String) : HomeScreenUiState()
}

data class MonthCalendarResult(
    val days: List<DayOfMonth>,
    val todayMorning: Boolean,
    val todayTraining: Boolean,
    val todayIndex: Int
)

data class DayOfMonth(
    val day: Int,
    val month: Int,
    val year: Int,
    val currentMonth: Boolean = false,
    val trainingState: Boolean = false,
    val morningState: Boolean = false
)

data class MorningDay(
    val id: Int,
    val date: String,
)