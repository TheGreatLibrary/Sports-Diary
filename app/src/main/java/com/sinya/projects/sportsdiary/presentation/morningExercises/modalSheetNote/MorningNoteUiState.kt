package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataMorning

sealed interface MorningNoteUiState {
    data object Loading : MorningNoteUiState

    data class Success(
        val items: List<DataMorning> = emptyList(),
        val visibleEditFieldId: Int? = null,
        val visibleAddField: Boolean = false,
        val query: String = "",
        val currentPlanId: Int? = null
    ) : MorningNoteUiState

    data class Error(val message: String) : MorningNoteUiState
}

