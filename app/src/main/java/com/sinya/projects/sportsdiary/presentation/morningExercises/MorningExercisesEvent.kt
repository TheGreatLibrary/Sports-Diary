package com.sinya.projects.sportsdiary.presentation.morningExercises

sealed interface MorningExercisesEvent {
    data class OnNoteExpanded(val state: Boolean) : MorningExercisesEvent
    data class OnPlanExpanded(val state: Boolean) : MorningExercisesEvent
}