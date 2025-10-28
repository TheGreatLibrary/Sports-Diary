package com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory

sealed class TrainingCategoryEvent {
    data class OnNameChange(val s: String) : TrainingCategoryEvent()
    data class OnQueryChange(val s: String) : TrainingCategoryEvent()
    data class Toggle(val id: Int) : TrainingCategoryEvent()
    data class SetAll(val checked: Boolean) : TrainingCategoryEvent()
    data object ToggleExpanded : TrainingCategoryEvent()
    data class CreateCategory(
        val onDone: () -> Unit,
        val onError: (Throwable) -> Unit
    ) : TrainingCategoryEvent()
}