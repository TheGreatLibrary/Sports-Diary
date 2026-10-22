package com.sinya.projects.proportions

import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.ui.components.sort.ModeOfSorting

internal sealed interface ProportionsUiState {
    data object Loading : ProportionsUiState

    data class Success(
        val proportions: List<ProportionsEntry> = emptyList(),
        val selectedMode: ModeOfSorting.Time = ModeOfSorting.Time(),
        val deleteItems: List<Int> = listOf(),
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false
    ) : ProportionsUiState {
    }
}
