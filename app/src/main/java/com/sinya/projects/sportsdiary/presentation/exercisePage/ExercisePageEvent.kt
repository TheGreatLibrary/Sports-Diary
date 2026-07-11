package com.sinya.projects.sportsdiary.presentation.exercisePage

sealed interface ExercisePageEvent {
    data class OnBodyInfoClick(val visible: Boolean = true) : ExercisePageEvent

}