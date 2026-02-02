package com.sinya.projects.sportsdiary.presentation.exercisePage

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.domain.model.ExerciseMusclesData

sealed interface ExercisePageUiState {
    data object Loading : ExercisePageUiState

    data class Success(
        val exercise: ExerciseTranslations,
        val exMuscles: List<ExerciseMusclesData> = emptyList()
    ) : ExercisePageUiState

    data class Error(val message: String) : ExercisePageUiState
}