package com.sinya.projects.routines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.managers.SettingsEngine
import com.sinya.projects.domain.useCase.DeleteRoutinesUseCase
import com.sinya.projects.domain.useCase.DuplicateRoutineUseCase
import com.sinya.projects.domain.useCase.ObserveCategoriesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class RoutinesViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine,
    private val deleteCategoryUseCase: DeleteRoutinesUseCase,
    private val duplicateRoutineUseCase: DuplicateRoutineUseCase,
    private val getCategoryListUseCase: ObserveCategoriesListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RoutinesUiState>(RoutinesUiState.Loading)
    val state: StateFlow<RoutinesUiState> = _state.asStateFlow()

    init {
        loadData()
        observeSettings()
    }

    private fun observeSettings() = viewModelScope.launch {
        settingsEngine.uiState.collectLatest { config ->
            updateIfSuccess { it.copy(currentPlanId = config.planMorningId) }
        }
    }


    fun onEvent(event: RoutinesEvent) {
        when (event) {

            RoutinesEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            RoutinesEvent.ReloadData -> loadData()

            RoutinesEvent.DeleteCategory -> delete()

            is RoutinesEvent.OnPlanClick -> {
                val state = state.value as? RoutinesUiState.Success ?: return
                if (state.currentPlanId == event.id) {
                    Log.d("RoutineScreen", "Success")
                    settingsEngine.setPlanMorningId(null)
                }
                else {
                    settingsEngine.setPlanMorningId(event.id)
                    Log.d("RoutineScreen", event.id.toString())
                }
            }

            RoutinesEvent.ClearedListToDelete -> updateIfSuccess {
                it.copy(
                    deleteItems = listOf(),
                    categories = it.categories.map { it.copy(selected = false) })
            }

            is RoutinesEvent.SelectProportion -> updateIfSuccess {
                val newList = it.deleteItems.toMutableSet()

                val selected = !it.categories.first { it.id == event.id }.selected

                if (selected) newList.add(event.id) else newList.remove(event.id)
                it.copy(
                    deleteItems = newList.toList(),
                    categories = it.categories.map {
                        if (it.id == event.id) it.copy(selected = selected) else it
                    }
                )
            }

            is RoutinesEvent.DuplicateRoutine -> duplicateRoutine(event.id)
        }
    }

    private fun duplicateRoutine(id: Int) = viewModelScope.launch {
        duplicateRoutineUseCase(id).fold(
            onSuccess = {
                Log.d("RoutineScreen", it.toString())
            },
            onFailure = { error ->
                updateIfSuccess { it.copy(errorMessage = error.toString()) }
            }
        )
    }

    private fun delete() = viewModelScope.launch {
        val currentState = _state.value as? RoutinesUiState.Success ?: return@launch

        if (currentState.deleteItems.isNotEmpty()) {
            val items = currentState.categories.filter {
                currentState.deleteItems.contains(it.id)
            }

            viewModelScope.launch {
                deleteCategoryUseCase(items).fold(
                    onSuccess = {
                        if (items.find { it.id == currentState.currentPlanId} != null) {
                            Log.d("Routine", "Success")
                            settingsEngine.setPlanMorningId(null)
                        }
                        updateIfSuccess { it.copy(deleteItems = listOf()) }
                    },
                    onFailure = { error ->
                        updateIfSuccess { it.copy(errorMessage = error.toString()) }
                    }
                )
            }
        }
    }

    private fun loadData() = viewModelScope.launch {
        updateIfSuccess { it.copy(isRefreshing = true) }

        getCategoryListUseCase().collectLatest { list ->
            val currentPlanId = settingsEngine.uiState.value.planMorningId

            _state.update { currentState ->
                if (currentState is RoutinesUiState.Success) {
                    currentState.copy(
                        categories = list,
                        currentPlanId = currentPlanId,
                        isRefreshing = false
                    )
                } else {
                    RoutinesUiState.Success(
                        categories = list,
                        currentPlanId = currentPlanId,
                        isRefreshing = false
                    )
                }
            }
        }
    }
    private fun updateIfSuccess(transform: (RoutinesUiState.Success) -> RoutinesUiState.Success) {
        _state.update { currentState ->
            if (currentState is RoutinesUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}