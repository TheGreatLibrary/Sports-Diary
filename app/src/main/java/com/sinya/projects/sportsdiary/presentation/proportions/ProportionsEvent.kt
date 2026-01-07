package com.sinya.projects.sportsdiary.presentation.proportions

sealed interface ProportionsEvent {
    data class YearChange(val year: Int) : ProportionsEvent
    data class MonthChange(val month: Int) : ProportionsEvent
    data class OpenDialog(val id: Int?) : ProportionsEvent
    data object DeleteProportion : ProportionsEvent
    data object ReloadData : ProportionsEvent
    data object OnErrorShown : ProportionsEvent
}
