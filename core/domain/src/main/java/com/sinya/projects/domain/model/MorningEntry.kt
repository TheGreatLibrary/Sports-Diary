package com.sinya.projects.domain.model

import java.time.LocalDate

data class MorningEntry(
    val id: Int = 0,
    val note: String?,
    val date: String,
    val categoryId: Int?
)

