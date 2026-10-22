package com.sinya.projects.domain.model

import java.time.LocalDate

data class CalendarWidgetUiState(
    val date: LocalDate,
    val monthDays: List<DayOfMonth>
)