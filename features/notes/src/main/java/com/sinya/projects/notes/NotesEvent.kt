package com.sinya.projects.notes

import com.sinya.projects.ui.components.sort.SortParam

sealed interface NotesEvent {
    data class SortParamChange(val onSelect: SortParam) : NotesEvent
    data class SelectNote(val id: Int) : NotesEvent
    data class SelectEditMode(val id: Int) : NotesEvent
    data class OnValueChange(val id: Int, val value: String) : NotesEvent
    data object DeleteNotes : NotesEvent
    data object ReloadData : NotesEvent
    data object OnErrorShown : NotesEvent
    data object ClearedListToDelete : NotesEvent
}