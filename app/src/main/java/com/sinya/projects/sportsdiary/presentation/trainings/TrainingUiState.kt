package com.sinya.projects.sportsdiary.presentation.trainings

sealed class TrainingUiState {
    data object Loading : TrainingUiState()
    data class Success(
        val mode: SortMode = SortMode.TIME,
        val trainings: List<Training> = emptyList()
    ) : TrainingUiState()
    data class Error(val message: String) : TrainingUiState()
}

sealed class TrainingUiEvent {
    data class ModeChange(val mode: SortMode) : TrainingUiEvent()
    data object ReloadData : TrainingUiEvent()
}

data class Training(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val category: String,
    val date: String?
)

enum class SortMode { TIME, MUSCLE }