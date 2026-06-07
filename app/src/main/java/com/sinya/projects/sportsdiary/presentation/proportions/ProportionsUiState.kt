package com.sinya.projects.sportsdiary.presentation.proportions

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Proportions
import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting

sealed interface ProportionsUiState {
    data object Loading : ProportionsUiState

    data class Success(
        val proportions: List<Proportions> = emptyList(),
        val selectedMode: ModeOfSorting.Time = ModeOfSorting.Time(),
        val deleteDialogId: Int? = null,
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false
    ) : ProportionsUiState
}
