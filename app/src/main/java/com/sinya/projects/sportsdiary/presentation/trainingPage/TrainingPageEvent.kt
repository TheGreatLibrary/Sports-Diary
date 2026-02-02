package com.sinya.projects.sportsdiary.presentation.trainingPage

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

sealed interface TrainingPageEvent {
    data object OpenBottomSheetCategory : TrainingPageEvent
    data object OpenBottomSheetTraining : TrainingPageEvent
    data class Delete(val id: Int) : TrainingPageEvent
    data class DeleteSet(val id: Int, val index: Int) : TrainingPageEvent
    data class AddSet(val id: Int) : TrainingPageEvent
    data class OnSelectedCategory(val category: TypeTraining?) : TrainingPageEvent
    data class EditSet(val exId: Int, val index: Int, val value: String?, val valState: Boolean) : TrainingPageEvent
    data class OpenDialog(val id: Int?) : TrainingPageEvent
    data class CalendarState(val state: Boolean) : TrainingPageEvent
    data class OnPickDate(val millis: Long?) : TrainingPageEvent

    data object UpdateCategories : TrainingPageEvent
    data class UpdateListTraining(val id: Int?) : TrainingPageEvent
    data class MoveExercise(val from: Int, val to: Int) : TrainingPageEvent
    data class OpenDialogGuide(val title: String, val descr: String) : TrainingPageEvent

    data object OnErrorShown : TrainingPageEvent
    data object Save : TrainingPageEvent

    // модальные окна

    data class Toggle(val id: Int) : TrainingPageEvent
    data class OnNameChange(val s: String) : TrainingPageEvent
    data class OnQueryChange(val s: String) : TrainingPageEvent
    data class OnCreateCategory(val onDone: () -> Unit) : TrainingPageEvent
    data object AddExercise : TrainingPageEvent
}