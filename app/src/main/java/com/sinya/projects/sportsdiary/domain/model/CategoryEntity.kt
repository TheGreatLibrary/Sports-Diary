package com.sinya.projects.sportsdiary.domain.model

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

data class CategoryEntity(
    val category: TypeTraining,
    val items: List<ExerciseItemWithoutList>
)