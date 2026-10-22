package com.sinya.projects.proportions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.domain.useCase.DeleteProportionsUseCase
import com.sinya.projects.domain.useCase.GetProportionsUseCase
import com.sinya.projects.ui.components.sort.ModeOfSorting
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class ProportionsViewModel @Inject constructor(
    private val getProportionsUseCase: GetProportionsUseCase,
    private val deleteProportionUseCase: DeleteProportionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProportionsUiState>(ProportionsUiState.Loading)
    val state: StateFlow<ProportionsUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: ProportionsEvent) {
        when (event) {
            is ProportionsEvent.SortParamChange -> {
                updateIfSuccess {
                    it.copy(
                        selectedMode = it.selectedMode.apply(event.onSelect) as ModeOfSorting.Time
                    )
                }
            }

            ProportionsEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            ProportionsEvent.ReloadData -> loadData()

            ProportionsEvent.DeleteProportion -> delete()

            ProportionsEvent.ClearedListToDelete -> updateIfSuccess {
                it.copy(
                    deleteItems = listOf(),
                    proportions = it.proportions.map { it.copy(selected = false) })
            }

            is ProportionsEvent.SelectProportion ->  updateIfSuccess {
                val newList = it.deleteItems.toMutableSet()

                val selected = !it.proportions.first { it.id == event.id }.selected

                if (selected) newList.add(event.id) else newList.remove(event.id)
                it.copy(
                    deleteItems = newList.toList(),
                    proportions = it.proportions.map {
                        if (it.id == event.id) it.copy(selected = selected) else it
                    }
                )
            }
        }
    }

    private fun delete() {
        val currentState = _state.value as? ProportionsUiState.Success ?: return

        if (currentState.deleteItems.isNotEmpty()) {
            val items = currentState.proportions.filter {
                currentState.deleteItems.contains(it.id)
            }.let { list ->
                list.map { item ->
                    ProportionsEntry(
                        id = item.id,
                        date = item.date
                    )
                }
            }

            viewModelScope.launch {
                deleteProportionUseCase(items).fold(
                    onSuccess = {
                        updateIfSuccess { it.copy(deleteItems = listOf()) }
                    },
                    onFailure = { error ->
                        updateIfSuccess { it.copy(errorMessage = error.toString()) }
                    }
                )
            }
        }
    }

    private fun loadData() {
        updateIfSuccess { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            getProportionsUseCase().collectLatest { list ->
                if (_state.value is ProportionsUiState.Success) {
                    updateIfSuccess {
                        it.copy(
                            proportions = list,
                            isRefreshing = false
                        )
                    }
                } else {
                    _state.update {
                        ProportionsUiState.Success(proportions = list, isRefreshing = false)
                    }
                }
            }
        }
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