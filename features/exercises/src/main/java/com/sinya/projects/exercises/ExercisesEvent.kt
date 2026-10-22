package com.sinya.projects.exercises

import com.sinya.projects.ui.components.sort.ModeOfSorting

internal sealed interface ExercisesEvent {
    data class OnQueryChange(val s: String) : ExercisesEvent
    data class SortParamChange(val mode: ModeOfSorting, val param: Any) : ExercisesEvent
    data object DeleteExercises : ExercisesEvent

    data class SelectTraining(val id: Int) : ExercisesEvent

    data object ReloadData : ExercisesEvent
    data object OnErrorShown : ExercisesEvent
}