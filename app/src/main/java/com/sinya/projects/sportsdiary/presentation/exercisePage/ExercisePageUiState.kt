package com.sinya.projects.sportsdiary.presentation.exercisePage

import com.sinya.projects.sportsdiary.core.domain.model.ExerciseMusclesData
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithFullData

sealed interface ExercisePageUiState {
    data object Loading : ExercisePageUiState

    data class Success(
        val exercise: ExerciseWithFullData,
        val exMuscles: List<ExerciseMusclesData> = emptyList()
    ) : ExercisePageUiState

    data class Error(val message: String) : ExercisePageUiState
}