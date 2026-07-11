package com.sinya.projects.sportsdiary.ui.features.map.model

data class DataExerciseMuscleRaw(
    val muscleId: Int,
    val name: String,
    val countResult: String,   // Текущая загрузка от 0.0f до 1.0f
    val weightResult: String,   // Текущая загрузка от 0.0f до 1.0f
    val level: Int,
    val weight: Float,
    val mode: Int,
    val state: Int
)