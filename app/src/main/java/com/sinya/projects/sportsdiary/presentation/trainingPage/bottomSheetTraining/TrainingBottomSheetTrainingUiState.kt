package com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining

import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory.TrainingBottomSheetCategoryUiEvent


sealed class TrainingBottomSheetTrainingUiState {
    data object Loading : TrainingBottomSheetTrainingUiState()
    data class Success(
        val id: Int,
        val expanded: Boolean = false,
        val query: String = "",
        val items: List<ExerciseUi> = emptyList()
    ) : TrainingBottomSheetTrainingUiState()
    data class Error(val message: String) : TrainingBottomSheetTrainingUiState()
}

sealed class TrainingBottomSheetTrainingUiEvent {
    data class OnQueryChange(val s: String) : TrainingBottomSheetTrainingUiEvent()
    data class Toggle(val id: Int) : TrainingBottomSheetTrainingUiEvent()
    data class SetAll(val checked: Boolean) : TrainingBottomSheetTrainingUiEvent()
    data object ToggleExpanded : TrainingBottomSheetTrainingUiEvent()
    data class AddTrainings(val onDone: () -> Unit, val onError: (Throwable) -> Unit) :
        TrainingBottomSheetTrainingUiEvent()
}

