package com.sinya.projects.pageExercise

sealed interface ExercisePageEvent {
    data class OnBodyInfoClick(val visible: Boolean = true) : ExercisePageEvent

}