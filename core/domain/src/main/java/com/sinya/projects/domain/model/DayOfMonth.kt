package com.sinya.projects.domain.model

import java.time.LocalDate

data class DayOfMonth(
    val date: LocalDate,
    val currentMonth: Boolean = false,
    val trainingState: Boolean = false,
    val morningState: Boolean = false,
    val note: String? = null,
    val state: Boolean = false
)