package com.sinya.projects.sportsdiary.presentation.proportionPage

import com.sinya.projects.sportsdiary.domain.model.ProportionDialogContent
import com.sinya.projects.sportsdiary.domain.model.ProportionItem

sealed interface ProportionPageUiState {
    data object Loading : ProportionPageUiState

    data class ProportionForm(
        val item: ProportionItem,
        val dialogContent: ProportionDialogContent? = null,
        val calendarVisible: Boolean = false,
        val errorMessage: String? = null
    ) : ProportionPageUiState

    data object Success : ProportionPageUiState

    data class Error(val errorMessage: String) : ProportionPageUiState
}