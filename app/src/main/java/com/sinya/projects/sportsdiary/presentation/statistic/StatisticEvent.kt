package com.sinya.projects.sportsdiary.presentation.statistic

sealed class StatisticEvent {
    data class OnSelectTimePeriod(val index: Int) : StatisticEvent()
    data class OnDialogState(val state: Boolean) : StatisticEvent()
}