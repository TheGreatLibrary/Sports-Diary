package com.sinya.projects.domain.model

import java.time.LocalDateTime

data class DayOfMonth(
    val date: LocalDateTime,
    val currentMonth: Boolean = false,
    val trainingState: Boolean = false,
    val morningState: Boolean = false,
    val note: String? = null,
    val state: Boolean = false
)