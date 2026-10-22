package com.sinya.projects.pagetraining.form

import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.ui.components.sort.ModeOfSorting


internal sealed interface TrainingFormEvent {

    data class OpenDialog(val id: Int?) : TrainingFormEvent

    data class OnSelectedCategory(val category: TypeTrainingEntry?) : TrainingFormEvent

    // модальные окна

    data object OnErrorShown : TrainingFormEvent
    data object Save : TrainingFormEvent
    data class OpenDialogGuide(val title: String, val descr: String) : TrainingFormEvent

    data class CalendarState(val state: Boolean) : TrainingFormEvent
    data object OpenBottomSheetCategory : TrainingFormEvent
    data object OpenBottomSheetTraining : TrainingFormEvent
    data class Toggle(val id: Int) : TrainingFormEvent
    data class OnNameChange(val s: String) : TrainingFormEvent

    data class OnQueryChange(val s: String) : TrainingFormEvent
    data object OnCreateCategory : TrainingFormEvent
    data object AddExercise : TrainingFormEvent
    data object Undo : TrainingFormEvent

    data class SortParamChange(val mode: ModeOfSorting, val param: Any) : TrainingFormEvent
    data class Undoable(val action: TrainingUndoableAction) : TrainingFormEvent
    data object Redo: TrainingFormEvent

}