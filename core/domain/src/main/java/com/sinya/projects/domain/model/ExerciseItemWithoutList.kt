package com.sinya.projects.domain.model

data class ExerciseItemWithoutList(
    val id: Int,
    val title: String,
    val mode: Int,
    val weight: Int,
    val orderIndex: Int
)