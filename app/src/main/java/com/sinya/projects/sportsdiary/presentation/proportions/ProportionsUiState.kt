package com.sinya.projects.sportsdiary.presentation.proportions

sealed class ProportionsUiState {
    data object Loading : ProportionsUiState()
    data class Success(
        val proportions: List<Proportion> = emptyList()
    ) : ProportionsUiState()
    data class Error(val message: String) : ProportionsUiState()
}

sealed class ProportionsUiEvent {

}

data class Proportion(
    val id: Int,
    val title: String,
    val date: String
)

