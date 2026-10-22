package com.sinya.projects.routines

internal sealed interface RoutinesEvent {
    data class OnPlanClick(val id: Int) : RoutinesEvent
    data class SelectProportion(val id: Int) : RoutinesEvent

    data object DeleteCategory : RoutinesEvent
    data object ReloadData : RoutinesEvent
    data object OnErrorShown : RoutinesEvent
    data object ClearedListToDelete : RoutinesEvent
}