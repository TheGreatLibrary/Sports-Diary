package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import com.sinya.projects.sportsdiary.data.database.entity.PlanMornings

sealed class ModalSheetPlanUiState {
    data object Loading : ModalSheetPlanUiState()
    data class Success(
        val items: List<PlanMornings> = emptyList(),
        val visibleEditFieldId: Int? = null,
        val visibleAddField: Boolean = false,
        val queryName: String = "",
        val queryDescription: String = ""
    ) : ModalSheetPlanUiState()
    data class Error(val message: String) : ModalSheetPlanUiState()
}

sealed class ModalSheetPlanEvent {
    data object OpenAddPlanField : ModalSheetPlanEvent()
    data object AddPlan : ModalSheetPlanEvent()
    data class OnQueryNameChange(val s: String) : ModalSheetPlanEvent()
    data class OnQueryDescriptionChange(val s: String) : ModalSheetPlanEvent()

    data class OpenEditPlanField(val id: Int) : ModalSheetPlanEvent()
    data class EditPlan(val id: Int) : ModalSheetPlanEvent()

    data class DeletePlan(val id: Int) : ModalSheetPlanEvent()
    data object ClearQuery : ModalSheetPlanEvent()
}