package com.sinya.projects.sportsdiary.presentation.exerciseEdit

import androidx.annotation.StringRes
import androidx.compose.ui.focus.FocusRequester
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ForceTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.LevelTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseEditItem
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseMusclesData

sealed interface ExerciseEditUiState {
    data object Loading : ExerciseEditUiState

    data class ExerciseForm(
        val exercise: ExerciseEditItem,
        val forces: List<ForceTranslation?>,
        val levels: List<LevelTranslation?>,
        val mechanics: List<MechanicTranslation?>,
        val equipments: List<EquipmentTranslation?>,
        val categories: List<CategoryTranslation?>,
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

