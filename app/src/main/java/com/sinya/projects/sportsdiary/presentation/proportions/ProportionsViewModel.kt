package com.sinya.projects.sportsdiary.presentation.proportions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.domain.useCase.DeleteProportionUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetProportionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProportionsViewModel @Inject constructor(
    private val getProportionsUseCase: GetProportionsUseCase,
    private val deleteProportionUseCase: DeleteProportionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProportionsUiState>(ProportionsUiState.Loading)
    val state: StateFlow<ProportionsUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: ProportionsEvent) {
        when (event) {
            is ProportionsEvent.MonthChange -> {
                updateIfSuccess { it.copy(selectedMode = it.selectedMode.copy(month = event.month)) }
            }

            is ProportionsEvent.YearChange -> {
                updateIfSuccess {
                    it.copy(
                        selectedMode = it.selectedMode.copy(
                            year = event.year,
                            month = -1
                        )
                    )
                }
            }

            is ProportionsEvent.OpenDialog -> updateIfSuccess { it.copy(deleteDialogId = event.id) }

            ProportionsEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            ProportionsEvent.ReloadData -> loadData()

            ProportionsEvent.DeleteProportion -> delete()
        }
    }

    private fun delete() = viewModelScope.launch {
        val currentState = _state.value as? ProportionsUiState.Success ?: return@launch

        currentState.deleteDialogId?.let { id ->
            val item = currentState.proportions.first { id == it.id }.let { item ->
                Proportions(
                    id = item.id,
                    date = item.date
                )
            }

            deleteProportionUseCase(item).fold(
                onSuccess = {
                    updateIfSuccess { it.copy(deleteDialogId = null) }
                    loadData()
                },
                onFailure = { error ->
                    updateIfSuccess { it.copy(errorMessage = error.toString()) }
                }
            )
        }
    }

    private fun loadData() = viewModelScope.launch {
        updateIfSuccess { it.copy(isRefreshing = true) }

        getProportionsUseCase().fold(
            onSuccess = { list ->
                if (_state.value is ProportionsUiState.Success) {
                    updateIfSuccess {
                        it.copy(
                            proportions = list,
                            isRefreshing = false
                        )
                    }
                } else {
                    _state.value = ProportionsUiState.Success(
                        proportions = list, isRefreshing = false
                    )
                }
            },
            onFailure = { error ->
                if (_state.value is ProportionsUiState.Success) {
                    updateIfSuccess {
                        it.copy(
                            errorMessage = error.toString(),
                            isRefreshing = false
                        )
                    }
                } else {
                    _state.value = ProportionsUiState.Success(
                        errorMessage = error.toString(),
                        isRefreshing = false
                    )
                }
            }
        )
    }

    private fun updateIfSuccess(transform: (ProportionsUiState.Success) -> ProportionsUiState.Success) {
        _state.update { currentState ->
            if (currentState is ProportionsUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}