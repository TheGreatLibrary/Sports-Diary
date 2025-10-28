package com.sinya.projects.sportsdiary.presentation.proportionPage

sealed class ProportionPageUiEvent {
    data object Save : ProportionPageUiEvent()
    data class OnChangeValue(val id: Int, val value: String) : ProportionPageUiEvent()
    data class OpenDialog(val id: Int?) : ProportionPageUiEvent()
}
