package com.sinya.projects.domain.model

import androidx.annotation.DrawableRes

data class DataExerciseMuscles(
    val muscleId: Int,
    val name: String,
    val value: Float,   // Текущая загрузка от 0.0f до 1.0f
    val level: Int,     // 0 = не участвует, 1 = побочная, 2 = целевая
    val centerX: Float,
    val centerY: Float,
    val radiusScale: Float = 0.15f,
    @DrawableRes val res: Int
)