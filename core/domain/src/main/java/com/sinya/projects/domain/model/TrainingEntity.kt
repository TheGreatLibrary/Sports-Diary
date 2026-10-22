package com.sinya.projects.domain.model


data class TrainingEntity(
    val id: Int? = null,
    val title: String,
    val category: TypeTrainingEntry?,
    val date: String,
    val items: List<ExerciseItem>
)