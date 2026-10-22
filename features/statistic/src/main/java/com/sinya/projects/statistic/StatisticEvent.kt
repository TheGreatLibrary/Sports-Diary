package com.sinya.projects.statistic

import androidx.annotation.StringRes
import com.sinya.projects.domain.enums.TypeTime

internal sealed interface StatisticEvent {
    data class OnBodyInfoClick(val visible: Boolean = true) : StatisticEvent
    data class OnSelectTimePeriod(val mode: TypeTime) : StatisticEvent
    data class OnMessage(@StringRes val message: Int?) : StatisticEvent
}