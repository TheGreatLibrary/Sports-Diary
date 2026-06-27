package com.sinya.projects.sportsdiary.presentation.exerciseEdit

sealed interface ExerciseEditEvent {

    data class OpenDialogGuide(val title: String, val descr: String) : ExerciseEditEvent

    data object DialogShown : ExerciseEditEvent

    data class ShowError(val message: Throwable?) : ExerciseEditEvent


    data object Save : ExerciseEditEvent

    data object Undo : ExerciseEditEvent

    data class Undoable(val action: ExerciseUndoableAction) : ExerciseEditEvent
    data object Redo : ExerciseEditEvent
}