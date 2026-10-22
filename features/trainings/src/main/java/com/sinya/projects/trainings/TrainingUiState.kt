package com.sinya.projects.trainings

import com.sinya.projects.ui.components.sort.ModeOfSorting
import com.sinya.projects.domain.model.Training

internal sealed interface TrainingUiState {
    data object Loading : TrainingUiState

    data class Success(
        val trainings: List<Training> = emptyList(),
        val selectedMode: ModeOfSorting = ModeOfSorting.Time(),
        val deleteItems: List<Int> = listOf(),
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false
    ) : TrainingUiState
}

