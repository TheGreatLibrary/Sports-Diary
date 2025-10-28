package com.sinya.projects.sportsdiary.presentation.proportions

import com.sinya.projects.sportsdiary.data.database.entity.Proportions

sealed class ProportionsUiState {
    data object Loading : ProportionsUiState()
    data class Success(
        val proportions: List<Proportions> = emptyList()
    ) : ProportionsUiState()
    data class Error(val message: String) : ProportionsUiState()
}
