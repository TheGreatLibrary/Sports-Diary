package com.sinya.projects.sportsdiary.presentation.categoryPage

import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting

sealed interface CategoryPageEvent {
    data class OpenDialog(val id: Int?) : CategoryPageEvent
    data object AddExercise : CategoryPageEvent
    data class OnQueryChange(val name: String) : CategoryPageEvent
    data class Toggle(val id: Int): CategoryPageEvent
    data class SortParamChange(val mode: ModeOfSorting, val param: Any) : CategoryPageEvent

    data object OnErrorShown : CategoryPageEvent
    data object Save : CategoryPageEvent

    data object Undo : CategoryPageEvent

    data class Undoable(val action: CategoryUndoableAction) : CategoryPageEvent
    data object Redo: CategoryPageEvent

}