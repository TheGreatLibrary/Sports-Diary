package com.sinya.projects.sportsdiary.widgets.calendarWidget

import com.sinya.projects.sportsdiary.presentation.home.DayOfMonth

data class CalendarWidgetUiState(
    val year: Int,
    val month: Int,
    val monthDays: List<DayOfMonth>,
    val morningState: Boolean = false,
    val trainingState: Boolean = false
)