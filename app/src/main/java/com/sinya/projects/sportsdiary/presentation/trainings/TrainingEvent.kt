package com.sinya.projects.sportsdiary.presentation.trainings

sealed class TrainingEvent {
    data class ModeChange(val mode: SortMode) : TrainingEvent()
    data object ReloadData : TrainingEvent()
}