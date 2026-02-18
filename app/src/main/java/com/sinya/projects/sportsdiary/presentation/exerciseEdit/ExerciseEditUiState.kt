package com.sinya.projects.sportsdiary.presentation.exerciseEdit

import com.sinya.projects.sportsdiary.data.database.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.data.database.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.data.database.entity.ForceTranslation
import com.sinya.projects.sportsdiary.data.database.entity.LevelTranslation
import com.sinya.projects.sportsdiary.data.database.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.domain.model.ExerciseEditItem
import com.sinya.projects.sportsdiary.domain.model.ExerciseMusclesData

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
        val dialogContent: ExerciseDialogContent? = null
    ): ExerciseEditUiState

    data class Success(val id: Int) : ExerciseEditUiState

    data class Error(val message: String) : ExerciseEditUiState
}

