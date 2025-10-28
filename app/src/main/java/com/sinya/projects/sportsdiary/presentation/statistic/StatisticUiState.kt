package com.sinya.projects.sportsdiary.presentation.statistic

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.ui.features.diagram.ChartPoint

sealed class StatisticUiState {
    data object Loading : StatisticUiState()
    data class Success(
        val countTrain: Int,
        val countWeight: Float,
        val exercises: List<ExerciseTranslations> = emptyList(),
        val timeMode: TimeMode = TimeMode.DAYS,
        val chartList: List<ChartPoint>,
        val chartLoading: Boolean = true,
        val dialogState: Boolean = false
    ) : StatisticUiState()
    data class Error(val message: String) : StatisticUiState()
}

data class DataTrainings(
    val count: String,
    val weight: String
)

data class ChartDataTrainings(
    val count: String,
    val weight: String,
    val date: String
)

enum class TimeMode(val index: Int) {
    DAYS(0),
    MONTHS(1),
    YEARS(2);

    companion object {
        fun fromIndex(index: Int): TimeMode =
            entries.find { it.index == index } ?: DAYS
    }
}