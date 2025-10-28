package com.sinya.projects.sportsdiary.presentation.trainingPage

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

sealed interface TrainingPageEvent {
    data class Save(val exit: () -> Unit) : TrainingPageEvent
    data class AddExercise(val title: String) : TrainingPageEvent
    data class OpenBottomSheetCategory(val state: Boolean) : TrainingPageEvent
    data class OpenBottomSheetTraining(val state: Boolean) : TrainingPageEvent
    data class Delete(val id: Int) : TrainingPageEvent
    data class DeleteSet(val id: Int, val index: Int) : TrainingPageEvent
    data class AddSet(val id: Int) : TrainingPageEvent
    data class OnSelectedCategory(val name: TypeTraining) : TrainingPageEvent
    data class EditSet(val exId: Int, val index: Int, val value: String?, val valState: Boolean) : TrainingPageEvent
    data object UpdateCategories : TrainingPageEvent
    data object UpdateListTraining : TrainingPageEvent
    data class OpenDialog(val id: Int?) : TrainingPageEvent
}