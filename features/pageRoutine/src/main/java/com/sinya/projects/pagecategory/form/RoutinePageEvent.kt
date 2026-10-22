package com.sinya.projects.pagecategory.form

import com.sinya.projects.ui.components.sort.ModeOfSorting

internal sealed interface RoutinePageEvent {
    data class OpenDialog(val id: Int?) : RoutinePageEvent
    data object AddExercise : RoutinePageEvent
    data class OnQueryChange(val name: String) : RoutinePageEvent
    data class Toggle(val id: Int): RoutinePageEvent
    data class SortParamChange(val mode: ModeOfSorting, val param: Any) : RoutinePageEvent

    data object OnErrorShown : RoutinePageEvent
    data object Save : RoutinePageEvent

    data object Undo : RoutinePageEvent

    data class Undoable(val action: RoutineUndoableAction) : RoutinePageEvent
    data object Redo: RoutinePageEvent

}