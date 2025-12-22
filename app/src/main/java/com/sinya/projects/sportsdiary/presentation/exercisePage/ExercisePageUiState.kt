package com.sinya.projects.sportsdiary.presentation.exercisePage

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.domain.repository.ExerciseMusclesData

sealed class ExercisePageUiState {
    data object Loading : ExercisePageUiState()
    data class Success(
        val exercise: ExerciseTranslations,
        val exMuscles: List<ExerciseMusclesData>
    ) : ExercisePageUiState()
    data class Error(val message: String) : ExercisePageUiState()
}