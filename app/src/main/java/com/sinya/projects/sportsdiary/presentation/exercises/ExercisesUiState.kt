package com.sinya.projects.sportsdiary.presentation.exercises

import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.domain.model.ModeOfSorting

sealed interface ExercisesUiState {
    data object Loading : ExercisesUiState

    data class Success(
        val query: String = "",
        val exercises: List<ExerciseWithMuscles> = emptyList(),
        val selectedModes: List<ModeOfSorting> = listOf(
            ModeOfSorting.Level(),
            ModeOfSorting.Category(),
            ModeOfSorting.Muscle(),
            ModeOfSorting.Equipment()
        ),
        val errorMessage: String? = null,
    ) : ExercisesUiState

    data class Error(val message: String) : ExercisesUiState
}