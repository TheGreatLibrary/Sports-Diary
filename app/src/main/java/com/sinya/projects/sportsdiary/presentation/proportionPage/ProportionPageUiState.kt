package com.sinya.projects.sportsdiary.presentation.proportionPage

sealed class ProportionPageUiState {
    data object Loading : ProportionPageUiState()
    data class Success(
       val item: ProportionItem
    ) : ProportionPageUiState()
    data class Error(val message: String) : ProportionPageUiState()
}

sealed class ProportionPageUiEvent {
    data object Save : ProportionPageUiEvent()
    data class OnChangeValue(val id: Int, val value: String) : ProportionPageUiEvent()

}

enum class Side { LEFT, RIGHT, NONE }

data class ProportionRow(
    val id: Int,
    val title: String,
    val value: String,
    val unitMeasure: String
)

data class ProportionItem(
    val id: Int = 0,
    val title: String = "",
    val date: String = "12/23/2024",
    val items: List<ProportionRow> = emptyList(),
)