package com.sinya.projects.sportsdiary.presentation.sportExercises

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations

sealed class SportExercisesUiState {
    data object Loading : SportExercisesUiState()
    data class Success(
        val exercises: List<ExerciseTranslations> = emptyList()
    ) : SportExercisesUiState()
    data class Error(val message: String) : SportExercisesUiState()
}

sealed class SportExercisesUiEvent {

}