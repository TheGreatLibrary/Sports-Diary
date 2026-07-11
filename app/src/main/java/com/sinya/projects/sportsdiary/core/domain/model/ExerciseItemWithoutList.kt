package com.sinya.projects.sportsdiary.core.domain.model

data class ExerciseItemWithoutList(
    val id: Int,
    val title: String,
    val mode: Int,
    val weight: Int,
    val orderIndex: Int
)