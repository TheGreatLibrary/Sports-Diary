package com.sinya.projects.sportsdiary.presentation.trainingPage

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting

sealed interface TrainingPageEvent {

    data class OpenDialog(val id: Int?) : TrainingPageEvent

    data class OnSelectedCategory(val category: TypeTraining?) : TrainingPageEvent

    // модальные окна

    data object OnErrorShown : TrainingPageEvent
    data object Save : TrainingPageEvent
    data class OpenDialogGuide(val title: String, val descr: String) : TrainingPageEvent

    data class CalendarState(val state: Boolean) : TrainingPageEvent
    data object OpenBottomSheetCategory : TrainingPageEvent
    data object OpenBottomSheetTraining : TrainingPageEvent
    data class Toggle(val id: Int) : TrainingPageEvent
    data class OnNameChange(val s: String) : TrainingPageEvent

    data class OnQueryChange(val s: String) : TrainingPageEvent
    data object OnCreateCategory : TrainingPageEvent
    data object AddExercise : TrainingPageEvent
    data object Undo : TrainingPageEvent

    data class SortParamChange(val mode: ModeOfSorting, val param: Any) : TrainingPageEvent
    data class Undoable(val action: TrainingUndoableAction) : TrainingPageEvent
    data object Redo: TrainingPageEvent

}