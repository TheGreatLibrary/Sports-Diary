package com.sinya.projects.statisticdiagram

import androidx.annotation.StringRes

internal sealed interface StatisticDiagramEvent {
    data class OnMessage(@StringRes val error: Int?) : StatisticDiagramEvent
    data class OnToggleMeasure(val id: Int) : StatisticDiagramEvent
    data class OnToggleExercise(val id: Int) : StatisticDiagramEvent
}