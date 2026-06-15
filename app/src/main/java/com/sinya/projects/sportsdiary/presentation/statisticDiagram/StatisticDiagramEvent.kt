package com.sinya.projects.sportsdiary.presentation.statisticDiagram

import androidx.annotation.StringRes

sealed interface StatisticDiagramEvent {
    data class OnMessage(@StringRes val error: Int?) : StatisticDiagramEvent
    data class OnToggleMeasure(val id: Int) : StatisticDiagramEvent
    data class OnToggleExercise(val id: Int) : StatisticDiagramEvent
}