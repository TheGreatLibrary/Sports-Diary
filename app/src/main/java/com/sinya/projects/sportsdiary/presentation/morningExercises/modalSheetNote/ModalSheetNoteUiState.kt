package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

import com.sinya.projects.sportsdiary.data.database.entity.DataMorning

sealed class ModalSheetNoteUiState {
    data object Loading : ModalSheetNoteUiState()
    data class Success(
        val items: List<DataMorning> = emptyList(),
        val visibleEditFieldId: Int? = null,
        val visibleAddField: Boolean = false,
        val query: String = ""
    ) : ModalSheetNoteUiState()
    data class Error(val message: String) : ModalSheetNoteUiState()
}

sealed class ModalSheetNoteEvent {
    data object OpenAddNoteField : ModalSheetNoteEvent()
    data object AddNote : ModalSheetNoteEvent()
    data class OnQueryChange(val s: String) : ModalSheetNoteEvent()

    data class OpenEditNoteField(val id: Int) : ModalSheetNoteEvent()
    data class EditNote(val id: Int) : ModalSheetNoteEvent()

    data class ClearNote(val id: Int) : ModalSheetNoteEvent()
    data object ClearQuery : ModalSheetNoteEvent()
}