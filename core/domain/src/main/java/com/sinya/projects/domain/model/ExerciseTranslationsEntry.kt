package com.sinya.projects.domain.model

data class ExerciseTranslationsEntry(
    val id: Int,
    val language: String,
    val name: String,
    val description: String,
    val rule: String
)