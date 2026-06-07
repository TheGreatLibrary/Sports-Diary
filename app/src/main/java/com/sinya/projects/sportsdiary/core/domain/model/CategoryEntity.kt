package com.sinya.projects.sportsdiary.core.domain.model

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining

data class CategoryEntity(
    val category: TypeTraining,
    val items: List<ExerciseItemWithoutList>
)