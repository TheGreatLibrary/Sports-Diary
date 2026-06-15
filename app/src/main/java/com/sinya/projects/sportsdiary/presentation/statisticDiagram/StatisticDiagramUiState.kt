package com.sinya.projects.sportsdiary.presentation.statisticDiagram

import androidx.annotation.StringRes
import com.sinya.projects.sportsdiary.ui.features.DropdownCheckboxItem
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine

sealed interface StatisticDiagramUiState {
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