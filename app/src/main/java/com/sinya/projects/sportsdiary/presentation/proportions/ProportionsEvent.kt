package com.sinya.projects.sportsdiary.presentation.proportions

import com.sinya.projects.sportsdiary.domain.model.SortParam

sealed interface ProportionsEvent {
    data class OpenDialog(val id: Int?) : ProportionsEvent
    data class SortParamChange(val onSelect: SortParam) : ProportionsEvent
    data object DeleteProportion : ProportionsEvent
    data object ReloadData : ProportionsEvent
    data object OnErrorShown : ProportionsEvent
}
