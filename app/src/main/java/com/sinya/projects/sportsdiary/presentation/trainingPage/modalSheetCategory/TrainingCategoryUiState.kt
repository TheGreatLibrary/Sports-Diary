package com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory

import androidx.compose.runtime.Stable

sealed class TrainingCategoryUiState {
    data object Loading : TrainingCategoryUiState()
    data class Success(
        val categoryName: String = "",
        val expanded: Boolean = false,
        val query: String = "",
        val items: List<ExerciseUi> = emptyList()
    ) : TrainingCategoryUiState()
    data class Error(val message: String) : TrainingCategoryUiState()
}

@Stable
data class ExerciseUi(
    val id: Int,
    val name: String,
    val checked: Boolean = false
)
