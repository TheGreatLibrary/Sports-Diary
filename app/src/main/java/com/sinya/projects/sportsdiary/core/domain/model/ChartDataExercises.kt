package com.sinya.projects.sportsdiary.core.domain.model

data class ChartDataExercises(
    val count: String,
    val weight: String,
    val exerciseId: Int,
    val trainingNumber: Int,
    val mode: Int,
    val weightState: Float,
    val date: String,
    val categoryName: String
)