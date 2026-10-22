package com.sinya.projects.editexercise

import android.net.Uri

sealed interface ExerciseEditEvent {

    data class OpenDialogGuide(val title: String, val descr: String) : ExerciseEditEvent

    data object DialogShown : ExerciseEditEvent

    data class ShowError(val message: Throwable?) : ExerciseEditEvent


    data object Save : ExerciseEditEvent

    data object Undo : ExerciseEditEvent

    data class Undoable(val action: ExerciseUndoableAction) : ExerciseEditEvent
    data class OnImagePicked(val uri: Uri, val context: android.content.Context) : ExerciseEditEvent

    data object Redo : ExerciseEditEvent
}