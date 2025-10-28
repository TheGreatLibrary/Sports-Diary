package com.sinya.projects.sportsdiary.presentation.morningExercises

sealed class MorningExercisesUiEvent {
    data class OnNoteExpanded(val state: Boolean) : MorningExercisesUiEvent()
    data class OnPlanExpanded(val state: Boolean) : MorningExercisesUiEvent()
}