package com.sinya.projects.domain.model

data class ExercisesEntry(
    val id: Int,
    val icon: String,
    val gifUrl: String,
    val isCustom: Boolean,
    val forceId: Int?,
    val levelId: Int?,
    val mechanicId: Int?,
    val equipmentId: Int?,
    val categoryId: Int?
)