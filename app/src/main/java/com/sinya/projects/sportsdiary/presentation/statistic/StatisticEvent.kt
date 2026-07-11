package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.annotation.StringRes
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime

sealed interface StatisticEvent {
    data class OnBodyInfoClick(val visible: Boolean = true) : StatisticEvent
    data class OnSelectTimePeriod(val mode: TypeTime) : StatisticEvent
    data class OnMessage(@StringRes val message: Int?) : StatisticEvent
}