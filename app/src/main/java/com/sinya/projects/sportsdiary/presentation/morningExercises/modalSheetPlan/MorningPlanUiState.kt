package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import com.sinya.projects.sportsdiary.data.database.entity.PlanMornings

sealed class MorningPlanUiState {
    data object Loading : MorningPlanUiState()
    data class Success(
        val items: List<PlanMornings> = emptyList(),
        val visibleEditFieldId: Int? = null,
        val visibleAddField: Boolean = false,
        val queryName: String = "",
        val queryDescription: String = ""
    ) : MorningPlanUiState()
    data class Error(val message: String) : MorningPlanUiState()
}