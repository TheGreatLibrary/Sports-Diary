package com.sinya.projects.sportsdiary.presentation.categoryPage

import com.sinya.projects.sportsdiary.domain.model.CategoryEntity
import com.sinya.projects.sportsdiary.domain.model.CategorySheetItem
import com.sinya.projects.sportsdiary.domain.model.ExerciseDialogContent

sealed interface CategoryPageUiState {
    data object Loading : CategoryPageUiState

    data class CategoryForm(
        val item: CategoryEntity,
        val sheetData: CategorySheetItem,

        val bottomSheetCategoryItemsState: Boolean = false,
        val dialogContent: ExerciseDialogContent? = null,

        val errorMessage: String? = null
    ) : CategoryPageUiState

    data object Success : CategoryPageUiState

    data class Error(val errorMessage: String) : CategoryPageUiState
}

