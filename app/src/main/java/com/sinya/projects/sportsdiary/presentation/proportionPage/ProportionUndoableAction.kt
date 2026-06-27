package com.sinya.projects.sportsdiary.presentation.proportionPage

import com.sinya.projects.sportsdiary.core.domain.model.UndoableAction

sealed interface ProportionUndoableAction : UndoableAction<ProportionPageUiState.ProportionForm> {
    override fun apply(state: ProportionPageUiState.ProportionForm): ProportionPageUiState.ProportionForm
    override fun revert(state: ProportionPageUiState.ProportionForm): ProportionPageUiState.ProportionForm

    data class OnValueChange(
        val proportionId: Int,
        val oldValue: String,
        val value: String
    ) : ProportionUndoableAction {
        override fun apply(state: ProportionPageUiState.ProportionForm): ProportionPageUiState.ProportionForm {
            val list = state.item.items.map {
                if (proportionId == it.id) it.copy(value = value)
                else it
            }

            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(items = list)
            )
        }

        override fun revert(state: ProportionPageUiState.ProportionForm): ProportionPageUiState.ProportionForm {
            val list = state.item.items.map {
                if (proportionId == it.id) it.copy(value = oldValue)
                else it
            }

            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(items = list)
            )
        }

    }

    data class OnPickDate(val date: String, val oldDate: String) : ProportionUndoableAction {
        override fun apply(state: ProportionPageUiState.ProportionForm): ProportionPageUiState.ProportionForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    date = date
                )
            )
        }

        override fun revert(state: ProportionPageUiState.ProportionForm): ProportionPageUiState.ProportionForm {
            return OnPickDate(oldDate, date).apply(state)
        }
    }
}