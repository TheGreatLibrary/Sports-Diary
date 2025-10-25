package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import com.sinya.projects.sportsdiary.data.database.entity.PlanMornings

sealed class ModalSheetPlanUiState {
    data object Loading : ModalSheetPlanUiState()
    data class Success(
        val items: List<PlanMornings> = emptyList()
    ) : ModalSheetPlanUiState()
    data class Error(val message: String) : ModalSheetPlanUiState()
}

sealed class ModalSheetPlanEvent {
    data class Toggle(val id: Int) : ModalSheetPlanEvent()
    data object AddOrUpdateNote : ModalSheetPlanEvent()
}