package com.sinya.projects.home

import com.sinya.projects.domain.model.DayOfMonth
import java.time.LocalDate

internal sealed interface HomeEvent {
    data class PickDay(val date: LocalDate) : HomeEvent
    data class OnExtended(val extended: Boolean) : HomeEvent
    data class OnButtonMorningClick(val date: LocalDate, val morningState: Boolean, val planId: Int?) : HomeEvent
    data class OnShift(val index: Long) : HomeEvent
    data object SaveNote : HomeEvent

    data class OnValueChange(val value: String) : HomeEvent
    data class OnCheckClick(val it: DayOfMonth) : HomeEvent
    data object ToggleListOfMorningDays : HomeEvent

    data object OnErrorShown : HomeEvent
}