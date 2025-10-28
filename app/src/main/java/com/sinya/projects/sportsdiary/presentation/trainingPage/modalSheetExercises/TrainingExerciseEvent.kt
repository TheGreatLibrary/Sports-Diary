package com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises

sealed class TrainingExerciseEvent {
    data class OnQueryChange(val s: String) : TrainingExerciseEvent()
    data class Toggle(val id: Int) : TrainingExerciseEvent()
    data class SetAll(val checked: Boolean) : TrainingExerciseEvent()
    data object ToggleExpanded : TrainingExerciseEvent()
    data class AddTrainings(val onDone: () -> Unit, val onError: (Throwable) -> Unit) :
        TrainingExerciseEvent()
}

