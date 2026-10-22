package com.sinya.projects.domain.model

data class CategorySheetItem(
    val query: String = "",
    val items: List<ExerciseWithMuscles> = emptyList()
)