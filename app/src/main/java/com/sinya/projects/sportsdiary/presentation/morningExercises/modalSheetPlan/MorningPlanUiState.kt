package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.PlanMornings

sealed interface MorningPlanUiState {
    data object Loading : MorningPlanUiState

    data class Success(
        val items: List<PlanMornings?> = emptyList(),
        val visibleEditFieldId: Int? = null,
        val visibleAddField: Boolean = false,
        val queryName: String = "",
        val queryDescription: String = "",
        val currentPlanId: Int? = null
    ) : MorningPlanUiState
    data class Error(val message: String) : MorningPlanUiState
}