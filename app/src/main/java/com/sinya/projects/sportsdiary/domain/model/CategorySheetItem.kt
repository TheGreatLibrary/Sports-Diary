package com.sinya.projects.sportsdiary.domain.model

data class CategorySheetItem(
    val query: String = "",
    val items: List<ExerciseUi> = emptyList()
)