package com.sinya.projects.sportsdiary.presentation.trainings

sealed class TrainingEvent {
    data class ModeChange(val mode: SortMode) : TrainingEvent()
    data class OpenDialog(val id: Int?) : TrainingEvent()
    data object DeleteTraining : TrainingEvent()
    data object ReloadData : TrainingEvent()
}