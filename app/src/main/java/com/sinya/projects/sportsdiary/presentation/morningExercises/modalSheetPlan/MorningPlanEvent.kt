package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

sealed interface ModalSheetPlanEvent {
    data object OpenAddPlanField : ModalSheetPlanEvent
    data object AddPlan : ModalSheetPlanEvent
    data class OnQueryNameChange(val s: String) : ModalSheetPlanEvent
    data class OnQueryDescriptionChange(val s: String) : ModalSheetPlanEvent

    data class OpenEditPlanField(val id: Int) : ModalSheetPlanEvent
    data class EditPlan(val id: Int) : ModalSheetPlanEvent

    data class DeletePlan(val id: Int) : ModalSheetPlanEvent
    data class OnPlanClick(val id: Int?) : ModalSheetPlanEvent

    data object ClearQuery : ModalSheetPlanEvent
}