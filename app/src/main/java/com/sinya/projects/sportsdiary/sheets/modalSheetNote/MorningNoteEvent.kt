package com.sinya.projects.sportsdiary.sheets.modalSheetNote

sealed interface ModalSheetNoteEvent {
    data object OpenAddNoteField : ModalSheetNoteEvent
    data object AddNote : ModalSheetNoteEvent

    data class OnQueryChange(val s: String) : ModalSheetNoteEvent

    data class OpenEditNoteField(val id: Int) : ModalSheetNoteEvent
    data class EditNote(val id: Int) : ModalSheetNoteEvent

    data class ClearNote(val id: Int) : ModalSheetNoteEvent
    data object ClearQuery : ModalSheetNoteEvent
}