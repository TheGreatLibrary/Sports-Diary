package com.sinya.projects.pagetraining.form

import androidx.annotation.StringRes
import com.sinya.projects.domain.model.BottomSheetCategoryData
import com.sinya.projects.domain.model.ExerciseDialogContent
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.model.TrainingEntity
import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.ui.components.sort.ModeOfSorting

internal sealed interface TrainingFormUiState {
    data object Loading : TrainingFormUiState

    data class TrainingForm(
        val item: TrainingEntity,
        val categories: List<TypeTrainingEntry?> = emptyList(),
        val items: List<ExerciseWithMuscles> = emptyList(),
        val calendarVisible: Boolean = false,
        val modes: List<ModeOfSorting> = listOf(
            ModeOfSorting.Level(),
            ModeOfSorting.Category(),
            ModeOfSorting.Muscle(),
            ModeOfSorting.Equipment()
        ),
        val bottomSheetCategoryStatus: BottomSheetCategoryData? = null,
        val bottomSheetTrainingQuery: String? = "",
        val hasUnsavedChanges: Boolean = true,
        val isSaving: Boolean = false,
        val dialogContent: ExerciseDialogContent? = null,
        val isUndoing: Boolean = false,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : TrainingFormUiState

    data object Success : TrainingFormUiState

    data class Error(val errorMessage: String) : TrainingFormUiState
}

