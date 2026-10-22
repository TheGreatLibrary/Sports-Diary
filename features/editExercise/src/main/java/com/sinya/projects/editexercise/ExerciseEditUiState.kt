package com.sinya.projects.editexercise

import androidx.annotation.StringRes
import androidx.compose.ui.focus.FocusRequester
import com.sinya.projects.domain.model.ExerciseDescriptionEntry
import com.sinya.projects.domain.model.ExerciseDialogContent
import com.sinya.projects.domain.model.ExerciseEditItem
import com.sinya.projects.domain.model.ExerciseMusclesData

sealed interface ExerciseEditUiState {
    data object Loading : ExerciseEditUiState

    data class ExerciseForm(
        val exercise: ExerciseEditItem,
        val forces: List<ExerciseDescriptionEntry?>,
        val levels: List<ExerciseDescriptionEntry?>,
        val mechanics: List<ExerciseDescriptionEntry?>,
        val equipments: List<ExerciseDescriptionEntry?>,
        val categories: List<ExerciseDescriptionEntry?>,
        val muscles: List<ExerciseMusclesData>,
        val focusRequesters: List<FocusRequester> = List(3) { FocusRequester() },
        val dialogContent: ExerciseDialogContent? = null,
        val hasUnsavedChanges: Boolean = false,
        val isUndoing: Boolean = false,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
        val isSaving: Boolean = false,
        @StringRes val errorMessage: Int? = null,
    ): ExerciseEditUiState

    data class Success(val id: Int) : ExerciseEditUiState

    data class Error(val message: String) : ExerciseEditUiState
}

