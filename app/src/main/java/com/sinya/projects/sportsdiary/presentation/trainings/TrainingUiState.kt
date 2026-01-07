package com.sinya.projects.sportsdiary.presentation.trainings

import com.sinya.projects.sportsdiary.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.domain.model.Training

sealed interface TrainingUiState {
    data object Loading : TrainingUiState

    data class Success(
        val trainings: List<Training> = emptyList(),
        val selectedMode: ModeOfSorting = ModeOfSorting.TimeMode(),
        val deleteDialogId: Int? = null,
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false
    ) : TrainingUiState
}

