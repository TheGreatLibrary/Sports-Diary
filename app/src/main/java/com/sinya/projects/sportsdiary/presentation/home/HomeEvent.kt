package com.sinya.projects.sportsdiary.presentation.home

import java.time.LocalDate

sealed interface HomeEvent {
    data class PickDay(val date: LocalDate) : HomeEvent
    data class OnExtended(val extended: Boolean) : HomeEvent
    data class OnButtonMorningClick(val date: LocalDate, val morningState: Boolean, val planId: Int) : HomeEvent
    data class OnShift(val index: Long) : HomeEvent
    data object OnErrorShown : HomeEvent
}