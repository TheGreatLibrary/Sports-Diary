package com.sinya.projects.sportsdiary.presentation.statistic

import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime

sealed interface StatisticEvent {
    data class OnSelectTimePeriod(val mode: TypeTime) : StatisticEvent
    data class OnDialogState(val state: Boolean) : StatisticEvent
    data object OnErrorShown : StatisticEvent
}