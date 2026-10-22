package com.sinya.projects.domain.model


data class CategoryEntity(
    val category: TypeTrainingEntry,
    val items: List<ExerciseItemWithoutList>
)