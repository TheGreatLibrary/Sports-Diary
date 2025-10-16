package com.sinya.projects.sportsdiary.presentation.morningExercises

sealed class MorningExercisesUiState {
    data object Loading : MorningExercisesUiState()
    data class Success(
        val countTraining: Int,
        val seriesScope: Int,
        val listPlan: List<String>,
        val listNote: List<String>
    ) : MorningExercisesUiState()
    data class Error(val message: String) : MorningExercisesUiState()
}

sealed class MorningExercisesUiEvent {

}
