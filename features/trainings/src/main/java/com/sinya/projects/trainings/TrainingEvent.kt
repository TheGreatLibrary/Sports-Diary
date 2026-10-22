package com.sinya.projects.trainings

import com.sinya.projects.ui.components.sort.SortParam

internal sealed interface TrainingEvent {
    data class ModeChange(val code: Int?) : TrainingEvent

    data class SortParamChange(val param: SortParam) : TrainingEvent
    data class SelectTraining(val id: Int) : TrainingEvent
    data object ClearedListToDelete : TrainingEvent

    data object DeleteTraining : TrainingEvent
    data object ReloadData : TrainingEvent
    data object OnErrorShown : TrainingEvent
}