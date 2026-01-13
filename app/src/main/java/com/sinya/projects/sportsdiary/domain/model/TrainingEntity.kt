package com.sinya.projects.sportsdiary.domain.model

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

data class TrainingEntity(
    val id: Int? = null,
    val title: String,
    val category: TypeTraining,
    val date: String,
    val items: List<ExerciseItem>
)