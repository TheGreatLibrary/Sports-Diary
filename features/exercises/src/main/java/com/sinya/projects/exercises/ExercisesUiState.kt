package com.sinya.projects.exercises

import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.ui.components.sort.ModeOfSorting

internal sealed interface ExercisesUiState {
    data object Loading : ExercisesUiState

    data class Success(
        val query: String = "",
        val exercises: List<ExerciseWithMuscles> = emptyList(),
        val selectedModes: List<ModeOfSorting> = listOf(
            ModeOfSorting.Level(),
            ModeOfSorting.Category(),
            ModeOfSorting.Muscle(),
            ModeOfSorting.Equipment(),
            ModeOfSorting.Custom()
        ),
        val isRefreshing: Boolean = false,
        val deleteItems: List<Int> = listOf(),
        val errorMessage: String? = null
    ) : ExercisesUiState {
    }
}