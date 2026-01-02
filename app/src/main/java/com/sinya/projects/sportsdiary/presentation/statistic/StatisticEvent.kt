package com.sinya.projects.sportsdiary.presentation.statistic

sealed interface StatisticEvent {
    data class OnSelectTimePeriod(val index: Int) : StatisticEvent
    data class OnDialogState(val state: Boolean) : StatisticEvent
    data object OnErrorShown : StatisticEvent
}