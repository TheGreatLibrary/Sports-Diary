package com.sinya.projects.sportsdiary.domain.model

data class ProportionDialogContent(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String? = null
)

data class ExerciseDialogContent(
    val id: Int,
    val name: String,
    val description: String
)