package com.sinya.projects.sportsdiary.presentation.morningExercises

sealed class MorningExercisesUiState {
    data object Loading : MorningExercisesUiState()
    data class Success(
        val countTraining: Int,
        val seriesScope: Int,
        val noteExpanded: Boolean = false,
        val planExpanded: Boolean = false
    ) : MorningExercisesUiState()
    data class Error(val message: String) : MorningExercisesUiState()
}