package com.sinya.projects.sportsdiary.presentation.exercises

import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting

sealed interface ExercisesEvent {
    data class OnQueryChange(val s: String) : ExercisesEvent
    data class SortParamChange(val mode: ModeOfSorting, val param: Any) : ExercisesEvent
    data object Delete : ExercisesEvent

    data class OpenDialog(val id: Int?) : ExercisesEvent

    data object ReloadData : ExercisesEvent
    data object OnErrorShown : ExercisesEvent
}