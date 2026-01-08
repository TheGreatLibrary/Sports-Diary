package com.sinya.projects.sportsdiary.presentation.proportionPage

sealed interface ProportionPageEvent {
    data object Save : ProportionPageEvent
    data object OnErrorShown : ProportionPageEvent
    data class OnChangeValue(val id: Int, val value: String) : ProportionPageEvent
    data class OpenDialog(val id: Int?) : ProportionPageEvent
    data class CalendarState(val state: Boolean) : ProportionPageEvent
    data class OnPickDate(val millis: Long?) : ProportionPageEvent
}