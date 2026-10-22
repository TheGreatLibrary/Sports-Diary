package com.sinya.projects.notes

import androidx.annotation.StringRes
import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.model.NoteItem
import com.sinya.projects.ui.components.sort.ModeOfSorting

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data class Success(
        val notes: List<NoteItem> = emptyList(),
        val selectedMode: ModeOfSorting.Time = ModeOfSorting.Time(),
        val deleteItems: List<Int> = listOf(),
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false
    ) : NotesUiState
}

