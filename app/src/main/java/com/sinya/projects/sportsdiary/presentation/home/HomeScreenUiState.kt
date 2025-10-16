package com.sinya.projects.sportsdiary.presentation.home

import com.sinya.projects.sportsdiary.presentation.trainings.Training

sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data class Success(
        val year: Int,
        val month: Int,
        val monthDays: List<DayOfMonth>,
        val trainingList: List<Training> = emptyList(),
        val morningState: Boolean = false,
        val trainingState: Boolean = false,
        val calendarExpanded: Boolean = false
    ) : HomeScreenUiState()
    data class Error(val message: String) : HomeScreenUiState()
}

data class DayOfMonth(
    val day: Int,
    val month: Int,
    val year: Int,
    val currentMonth: Boolean = false,
    val trainingState: Boolean = false,
    val morningState: Boolean = false
)

data class CardOfTraining(
    val id: Int,
    val name: String,
    val date: String,
)

sealed class HomeScreenUiEvent {
    data class OnExtended(val extended: Boolean) : HomeScreenUiEvent()
    data class OnButtonMorningClick(val morningState: Boolean) : HomeScreenUiEvent()
    data class OnShift(val index: Int) : HomeScreenUiEvent()
    data object UpdateTrainingCard : HomeScreenUiEvent()
}

data class MonthCalendarResult(
    val days: List<DayOfMonth>,
    val todayMorning: Boolean,
    val todayTraining: Boolean,
    val todayIndex: Int
)


data class MorningDay(
    val id: Int,
    val date: String
)