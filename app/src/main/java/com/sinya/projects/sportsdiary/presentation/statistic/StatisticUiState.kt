package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.annotation.StringRes
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine

sealed interface StatisticUiState {
    data object Loading : StatisticUiState

    data class Success(
        val countTrain: Int = 0,
        val countWeight: Float = 0f,
        val timeMode: TypeTime = TypeTime.DAYS,
        val chartList: List<ChartLine> = emptyList(),
        val chartLoading: Boolean = true,
        @StringRes val errorMessage: Int? = null,
    ) : StatisticUiState
}