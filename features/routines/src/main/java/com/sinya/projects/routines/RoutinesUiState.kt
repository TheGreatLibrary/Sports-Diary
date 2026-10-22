package com.sinya.projects.routines

import com.sinya.projects.domain.model.TypeTrainingEntry

internal sealed interface RoutinesUiState {
    data object Loading : RoutinesUiState

    data class Success(
        val categories: List<TypeTrainingEntry> = emptyList(),
        val deleteItems: List<Int> = listOf(),
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false,
        val currentPlanId: Int? = null
    ) : RoutinesUiState
}