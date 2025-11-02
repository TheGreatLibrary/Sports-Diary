package com.sinya.projects.sportsdiary.presentation.proportions

sealed class ProportionsEvent {
    data class OpenDialog(val id: Int?) : ProportionsEvent()
    data object DeleteProportion : ProportionsEvent()
}
