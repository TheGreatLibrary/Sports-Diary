package com.sinya.projects.sportsdiary.core.domain.model

import androidx.compose.runtime.Stable

@Stable
data class ExerciseMusclesData(
    val muscleId: Int,
    val name: String,
    val value: Int,
    val checked: Boolean = false
)