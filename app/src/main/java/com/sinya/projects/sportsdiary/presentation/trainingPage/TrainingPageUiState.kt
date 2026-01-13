package com.sinya.projects.sportsdiary.presentation.trainingPage

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.domain.model.TrainingEntity

sealed interface TrainingPageUiState {
    data object Loading : TrainingPageUiState

    data class TrainingForm(
        val item: TrainingEntity,
        val categories: List<TypeTraining> = emptyList(),

        val bottomSheetCategoryStatus: Boolean = false,
        val bottomSheetTrainingStatus: Boolean = false,
        val calendarVisible: Boolean = false,
        val dialogContent: ExerciseDialogContent? = null,

        val errorMessage: String? = null
    ) : TrainingPageUiState

    data object Success : TrainingPageUiState

    data class Error(val errorMessage: String) : TrainingPageUiState
}