package com.sinya.projects.domain.model

data class ExerciseEditItem(
    val id: Int? = null,
    val name: String = "",
    val isError: Boolean = false,
    val rule: String = "",
    val icon: String? = null,
    val gif: String = "",
    val description: String = "",
    val force: ExerciseDescriptionEntry?,
    val level: ExerciseDescriptionEntry?,
    val mechanic: ExerciseDescriptionEntry?,
    val equipment: ExerciseDescriptionEntry?,
    val category: ExerciseDescriptionEntry?,
    val isCustom: Boolean = true
)