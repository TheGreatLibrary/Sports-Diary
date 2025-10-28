package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

import com.sinya.projects.sportsdiary.data.database.entity.DataMorning

sealed class MorningNoteUiState {
    data object Loading : MorningNoteUiState()
    data class Success(
        val items: List<DataMorning> = emptyList(),
        val visibleEditFieldId: Int? = null,
        val visibleAddField: Boolean = false,
        val query: String = ""
    ) : MorningNoteUiState()
    data class Error(val message: String) : MorningNoteUiState()
}

