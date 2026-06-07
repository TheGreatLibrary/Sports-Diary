package com.sinya.projects.sportsdiary.core.domain.model

data class ExerciseWithSortedData(
    val id: Int,
    val name: String,
    val isCustom: Boolean = false,
    val level: String?,
    val equipment: String?,
    val category: String?
)

