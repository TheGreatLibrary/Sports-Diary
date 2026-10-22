package com.sinya.projects.pageproportion

import androidx.annotation.StringRes
import com.sinya.projects.domain.model.ProportionDialogContent
import com.sinya.projects.domain.model.ProportionItem

internal sealed interface ProportionPageUiState {
    data object Loading : ProportionPageUiState

    data class ProportionForm(
        val item: ProportionItem,
        val dialogContent: ProportionDialogContent? = null,
        val calendarVisible: Boolean = false,
        val hasUnsavedChanges: Boolean = false,
        val isSaving: Boolean = false,
        val isUndoing: Boolean = false,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
        @StringRes val errorMessage: Int? = null
    ) : ProportionPageUiState

    data object Success : ProportionPageUiState

    data class Error(val errorMessage: String) : ProportionPageUiState
}