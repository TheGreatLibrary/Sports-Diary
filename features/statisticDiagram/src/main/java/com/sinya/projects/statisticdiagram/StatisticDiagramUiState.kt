package com.sinya.projects.statisticdiagram

import androidx.annotation.StringRes
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.DropdownCheckboxItem

internal sealed interface StatisticDiagramUiState {
    data object Loading : StatisticDiagramUiState

    data class Success(
        val exercises: List<DropdownCheckboxItem> = emptyList(),
        val exercisesData: List<ChartLine> = emptyList(),
        val measurements: List<DropdownCheckboxItem> = emptyList(),
        val measurementsData: List<ChartLine> = emptyList(),
        val chartLoading: Boolean = true,
        @StringRes val errorMessage: Int? = null,
        val measureMenuDialog: Boolean = false,
    ) : StatisticDiagramUiState
}