package com.sinya.projects.domain.model

import java.time.LocalDateTime

data class CalendarWidgetUiState(
    val date: LocalDateTime?,
    val monthDays: List<DayOfMonth>
)