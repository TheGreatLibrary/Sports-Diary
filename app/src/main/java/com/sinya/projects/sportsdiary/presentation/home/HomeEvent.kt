package com.sinya.projects.sportsdiary.presentation.home

sealed class HomeEvent {
    data class OnExtended(val extended: Boolean) : HomeEvent()
    data class OnButtonMorningClick(val morningState: Boolean, val planId: Int) : HomeEvent()
    data class OnShift(val index: Int) : HomeEvent()
    data object UpdateTrainingCard : HomeEvent()
}