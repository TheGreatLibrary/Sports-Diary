package com.sinya.projects.sportsdiary.presentation.trainingPage

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.model.BottomSheetCategoryData
import com.sinya.projects.sportsdiary.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.domain.model.ExerciseUi
import com.sinya.projects.sportsdiary.domain.model.TrainingEntity

sealed interface TrainingPageUiState {
    data object Loading : TrainingPageUiState

    data class TrainingForm(
        val item: TrainingEntity,
        val categories: List<TypeTraining?> = emptyList(),
        val items: List<ExerciseUi> = emptyList(),
        val calendarVisible: Boolean = false,

        val bottomSheetCategoryStatus: BottomSheetCategoryData? = null,
        val bottomSheetTrainingQuery: String? = "",
        val dialogContent: ExerciseDialogContent? = null,
        val errorMessage: String? = null
    ) : TrainingPageUiState

    data object Success : TrainingPageUiState

    data class Error(val errorMessage: String) : TrainingPageUiState
}

