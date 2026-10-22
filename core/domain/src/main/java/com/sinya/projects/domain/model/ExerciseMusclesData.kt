package com.sinya.projects.domain.model


data class ExerciseMusclesData(
    val muscleId: Int,
    val name: String,
    val value: Int,
    val checked: Boolean = false
)