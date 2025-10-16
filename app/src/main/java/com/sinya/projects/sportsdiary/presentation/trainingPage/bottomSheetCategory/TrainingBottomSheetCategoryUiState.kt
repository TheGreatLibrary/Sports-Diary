package com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory

import androidx.compose.runtime.Stable


sealed class TrainingBottomSheetCategoryUiState {
    data object Loading : TrainingBottomSheetCategoryUiState()
    data class Success(
        val categoryName: String = "",
        val expanded: Boolean = false,
        val query: String = "",
        val items: List<ExerciseUi> = emptyList()
    ) : TrainingBottomSheetCategoryUiState()
    data class Error(val message: String) : TrainingBottomSheetCategoryUiState()
}

sealed class TrainingBottomSheetCategoryUiEvent {
    data class OnNameChange(val s: String) : TrainingBottomSheetCategoryUiEvent()
    data class OnQueryChange(val s: String) : TrainingBottomSheetCategoryUiEvent()
    data class Toggle(val id: Int) : TrainingBottomSheetCategoryUiEvent()
    data class SetAll(val checked: Boolean) : TrainingBottomSheetCategoryUiEvent()
    data object ToggleExpanded : TrainingBottomSheetCategoryUiEvent()
    data class CreateCategory(val onDone: () -> Unit, val onError: (Throwable) -> Unit) :
        TrainingBottomSheetCategoryUiEvent()
}

@Stable
data class ExerciseUi(
    val id: Int,
    val name: String,
    val checked: Boolean = false
)
