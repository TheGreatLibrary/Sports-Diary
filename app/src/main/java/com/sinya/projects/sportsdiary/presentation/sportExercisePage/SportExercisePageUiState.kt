package com.sinya.projects.sportsdiary.presentation.sportExercisePage

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations

sealed class SportExercisePageUiState {
    data object Loading : SportExercisePageUiState()
    data class Success(
        val exercise: ExerciseTranslations
    ) : SportExercisePageUiState()
    data class Error(val message: String) : SportExercisePageUiState()
}

sealed class SportExercisePageUiEvent {

}