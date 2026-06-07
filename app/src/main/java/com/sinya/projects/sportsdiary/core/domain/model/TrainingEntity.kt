package com.sinya.projects.sportsdiary.core.domain.model

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining

data class TrainingEntity(
    val id: Int? = null,
    val title: String,
    val category: TypeTraining?,
    val date: String,
    val items: List<ExerciseItem>
)