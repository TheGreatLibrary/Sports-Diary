package com.sinya.projects.sportsdiary.widgets.calendarWidget

import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import java.time.LocalDate

data class CalendarWidgetUiState(
    val date: LocalDate,
    val monthDays: List<DayOfMonth>
)