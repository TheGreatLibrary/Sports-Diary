package com.sinya.projects.domain.model

data class ExerciseWithFullData(
    val id: Int,
    val name: String,
    val description: String,
    val isCustom: Boolean,
    val icon: String?,
    val gif: String,
    val rule: String,
    val force: String?,
    val level: String?,
    val mechanic: String?,
    val equipment: String?,
    val category: String?
)