package com.sinya.projects.sportsdiary.presentation.exercises

sealed class ExercisesEvent {
    data class OnQueryChange(val s: String) : ExercisesEvent()
}