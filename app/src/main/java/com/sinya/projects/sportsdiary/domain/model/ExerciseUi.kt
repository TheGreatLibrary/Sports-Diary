package com.sinya.projects.sportsdiary.domain.model

import androidx.compose.runtime.Stable

@Stable
data class ExerciseUi(
    val id: Int,
    val name: String,
    val checked: Boolean = false
)