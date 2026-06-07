package com.sinya.projects.sportsdiary.presentation.categoryPage

import com.sinya.projects.sportsdiary.core.domain.model.CategoryEntity
import com.sinya.projects.sportsdiary.core.domain.model.CategorySheetItem
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseDialogContent

sealed interface CategoryPageUiState {
    data object Loading : CategoryPageUiState

    data class CategoryForm(
        val item: CategoryEntity,
        val sheetData: CategorySheetItem,
        val dialogContent: ExerciseDialogContent? = null,
        val errorMessage: String? = null,
        val isError: Boolean = false
    ) : CategoryPageUiState

    data object Success : CategoryPageUiState

    data class Error(val errorMessage: String) : CategoryPageUiState
}

