package com.sinya.projects.proportions

import com.sinya.projects.ui.components.sort.SortParam

internal sealed interface ProportionsEvent {
    data class SortParamChange(val onSelect: SortParam) : ProportionsEvent
    data class SelectProportion(val id: Int) : ProportionsEvent

    data object DeleteProportion : ProportionsEvent
    data object ReloadData : ProportionsEvent
    data object OnErrorShown : ProportionsEvent
    data object ClearedListToDelete : ProportionsEvent
}
