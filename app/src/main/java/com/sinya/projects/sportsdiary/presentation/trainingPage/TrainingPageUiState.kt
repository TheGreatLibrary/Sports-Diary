package com.sinya.projects.sportsdiary.presentation.trainingPage

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.model.BottomSheetCategoryData
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity

sealed interface TrainingPageUiState {
    data object Loading : TrainingPageUiState

    data class TrainingForm(
        val item: TrainingEntity,
        val categories: List<TypeTraining?> = emptyList(),
        val items: List<ExerciseWithMuscles> = emptyList(),
        val calendarVisible: Boolean = false,
        val trainingWarningState: Boolean,
        val modes: List<ModeOfSorting> = listOf(
            ModeOfSorting.Level(),
            ModeOfSorting.Category(),
            ModeOfSorting.Muscle(),
            ModeOfSorting.Equipment()
        ),
        val bottomSheetCategoryStatus: BottomSheetCategoryData? = null,
        val bottomSheetTrainingQuery: String? = "",
        val deleteDialogId: Int? = null,
        val dialogContent: ExerciseDialogContent? = null,
        val errorMessage: String? = null
    ) : TrainingPageUiState

    data object Success : TrainingPageUiState

    data class Error(val errorMessage: String) : TrainingPageUiState
}

