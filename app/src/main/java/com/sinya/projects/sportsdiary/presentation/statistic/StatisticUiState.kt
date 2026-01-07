package com.sinya.projects.sportsdiary.presentation.statistic

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.ui.features.diagram.ChartPoint

sealed interface StatisticUiState {
    data object Loading : StatisticUiState

    data class Success(
        val countTrain: Int = 0,
        val countWeight: Float = 0f,
        val exercises: List<ExerciseTranslations> = emptyList(),
        val timeMode: TypeTime = TypeTime.DAYS,
        val chartList: List<ChartPoint> = emptyList(),
        val chartLoading: Boolean = true,
        val dialogState: Boolean = false,
        val errorMessage: String? = null
    ) : StatisticUiState
}