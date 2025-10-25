package com.sinya.projects.sportsdiary.widgets

import com.sinya.projects.sportsdiary.presentation.home.DayOfMonth
import java.time.YearMonth

data class CalendarWidgetState(
    val year: Int,
    val month: Int,
    val monthDays: List<DayOfMonth>,
    val morningState: Boolean = false,
    val trainingState: Boolean = false
)