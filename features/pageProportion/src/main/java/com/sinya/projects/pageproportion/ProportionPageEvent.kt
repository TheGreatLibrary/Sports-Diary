package com.sinya.projects.pageproportion

internal sealed interface ProportionPageEvent {
    data object Save : ProportionPageEvent

    data object OnErrorShown : ProportionPageEvent

    data class OpenDialog(val id: Int?) : ProportionPageEvent
    data class CalendarState(val state: Boolean) : ProportionPageEvent


    data class Undoable(val action: ProportionUndoableAction) : ProportionPageEvent

    data object Undo : ProportionPageEvent

    data object Redo: ProportionPageEvent
}