package com.sinya.projects.sportsdiary.presentation.proportions

import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.domain.model.ModeOfSorting

sealed interface ProportionsUiState {
    data object Loading : ProportionsUiState

    data class Success(
        val proportions: List<Proportions> = emptyList(),
        val selectedMode: ModeOfSorting.TimeMode = ModeOfSorting.TimeMode(),
        val deleteDialogId: Int? = null,
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false
    ) : ProportionsUiState
}
