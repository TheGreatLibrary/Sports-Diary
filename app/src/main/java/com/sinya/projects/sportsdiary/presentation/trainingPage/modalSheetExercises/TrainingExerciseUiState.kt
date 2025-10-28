package com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises

import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi

sealed class TrainingExerciseUiState {
    data object Loading : TrainingExerciseUiState()
    data class Success(
        val id: Int,
        val expanded: Boolean = false,
        val query: String = "",
        val items: List<ExerciseUi> = emptyList()
    ) : TrainingExerciseUiState()
    data class Error(val message: String) : TrainingExerciseUiState()
}