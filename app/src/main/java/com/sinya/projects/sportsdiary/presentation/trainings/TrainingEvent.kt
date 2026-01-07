package com.sinya.projects.sportsdiary.presentation.trainings

import com.sinya.projects.sportsdiary.domain.enums.SortMode

sealed interface TrainingEvent {
    data class ModeChange(val mode: SortMode) : TrainingEvent
    data class CategoryChange(val category: String) : TrainingEvent
    data class YearChange(val year: Int) : TrainingEvent
    data class MonthChange(val month: Int) : TrainingEvent
    data class OpenDialog(val id: Int?) : TrainingEvent
    data object DeleteTraining : TrainingEvent
    data object ReloadData : TrainingEvent
    data object OnErrorShown : TrainingEvent
}