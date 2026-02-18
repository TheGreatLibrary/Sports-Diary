package com.sinya.projects.sportsdiary.domain.model

data class CategorySheetItem(
    val query: String = "",
    val items: List<ExerciseWithMuscles> = emptyList(),
    val modes: List<ModeOfSorting> = listOf(
        ModeOfSorting.Level(),
        ModeOfSorting.Category(),
        ModeOfSorting.Muscle(),
        ModeOfSorting.Equipment()
    )
)