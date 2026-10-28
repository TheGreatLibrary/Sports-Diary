package com.sinya.projects.statistic

import androidx.annotation.StringRes
import com.sinya.projects.domain.enums.TypeTime
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.DataExerciseMuscles

internal sealed interface StatisticUiState {
    data object Loading : StatisticUiState

    data class Success(
        val countTrain: Int = 0,
        val countWeight: Float = 0f,
        val countMorning: Int = 0,
        val seriesScope: Int = 0,
        val timeMode: TypeTime = TypeTime.DAYS,
        val chartList: List<ChartLine> = emptyList(),
        val dataExerciseMuscles: List<DataExerciseMuscles> = emptyList(),
        val chartLoading: Boolean = true,
        val bodyVisible: Boolean = false,
        @StringRes val errorMessage: Int? = null,
        val allMuscles: List<Pair<Int, String>>,
        val mapState: Boolean,
    ) : StatisticUiState
}