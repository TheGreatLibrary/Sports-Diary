package com.sinya.projects.sportsdiary.presentation.exercises

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations

sealed class ExercisesUiState {
    data object Loading : ExercisesUiState()
    data class Success(
        val exercises: List<ExerciseTranslations> = emptyList(),
        val query: String = ""
    ) : ExercisesUiState()
    data class Error(val message: String) : ExercisesUiState()
}