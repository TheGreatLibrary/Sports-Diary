package com.sinya.projects.sportsdiary.domain.model

data class ExerciseItemData(
    val exerciseId: Int,
    val index: Int,
    val count: String,
    val weight: String,
    val prevCount: String? = null,
    val prevWeight: String? = null
)