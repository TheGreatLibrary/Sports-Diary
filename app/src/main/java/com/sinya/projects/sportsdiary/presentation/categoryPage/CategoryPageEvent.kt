package com.sinya.projects.sportsdiary.presentation.categoryPage

sealed interface CategoryPageEvent {
    data object OpenBottomSheetTraining : CategoryPageEvent
    data class Delete(val id: Int) : CategoryPageEvent
    data class OnValueChange(val name: String) : CategoryPageEvent
    data class OpenDialog(val id: Int?) : CategoryPageEvent

    data class UpdateListTraining(val id: Int?) : CategoryPageEvent
    data class MoveExercise(val from: Int, val to: Int) : CategoryPageEvent
    data object AddExercise : CategoryPageEvent
    data class OnQueryChange(val name: String) : CategoryPageEvent
    data class Toggle(val id: Int): CategoryPageEvent

    data object OnErrorShown : CategoryPageEvent
    data object Save : CategoryPageEvent
}