package com.sinya.projects.sportsdiary.presentation.categories

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

sealed interface CategoriesUiState {
    data object Loading : CategoriesUiState

    data class Success(
        val categories: List<TypeTraining> = emptyList(),
        val deleteDialogId: Int? = null,
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false
    ) : CategoriesUiState
}