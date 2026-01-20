package com.sinya.projects.sportsdiary.domain.model

import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi

data class CategorySheetItem(
    val query: String = "",
    val items: List<ExerciseUi> = emptyList()
)