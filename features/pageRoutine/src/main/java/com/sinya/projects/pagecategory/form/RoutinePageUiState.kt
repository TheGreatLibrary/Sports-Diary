package com.sinya.projects.pagecategory.form

import androidx.annotation.StringRes
import com.sinya.projects.domain.model.CategoryEntity
import com.sinya.projects.domain.model.CategorySheetItem
import com.sinya.projects.domain.model.ExerciseDialogContent
import com.sinya.projects.ui.components.sort.ModeOfSorting

internal sealed interface RoutinePageUiState {
    data object Loading : RoutinePageUiState

    data class RoutineForm(
        val item: CategoryEntity,
        val sheetData: CategorySheetItem,
        val dialogContent: ExerciseDialogContent? = null,
        val hasUnsavedChanges: Boolean = false,
        val isSaving: Boolean = false,
        @StringRes val errorMessage: Int? = null,
        val isUndoing: Boolean = false,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
        val isError: Boolean = false,
        val modes: List<ModeOfSorting> = listOf(
            ModeOfSorting.Level(),
            ModeOfSorting.Category(),
            ModeOfSorting.Muscle(),
            ModeOfSorting.Equipment()
        )
    ) : RoutinePageUiState

    data object Success : RoutinePageUiState

    data class Error(val errorMessage: String) : RoutinePageUiState
}

