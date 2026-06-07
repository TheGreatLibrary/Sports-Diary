package com.sinya.projects.sportsdiary.presentation.trainings

import com.sinya.projects.sportsdiary.core.domain.model.SortParam

sealed interface TrainingEvent {
    data class ModeChange(val code: Int?) : TrainingEvent
    data class OpenDialog(val id: Int?) : TrainingEvent

    data class SortParamChange(val param: SortParam) : TrainingEvent

    data object DeleteTraining : TrainingEvent
    data object ReloadData : TrainingEvent
    data object OnErrorShown : TrainingEvent
}