package com.sinya.projects.pageExercise

import com.sinya.projects.domain.model.ExerciseMusclesData
import com.sinya.projects.domain.model.ExerciseWithFullData

sealed interface ExercisePageUiState {
    data object Loading : ExercisePageUiState

    data class Success(
        val exercise: ExerciseWithFullData,
        val exMuscles: List<ExerciseMusclesData> = emptyList(),
        val bodyVisible: Boolean = false,
        val allMuscles: List<Pair<Int, String>>
    ) : ExercisePageUiState

    data class Error(val message: String) : ExercisePageUiState
}