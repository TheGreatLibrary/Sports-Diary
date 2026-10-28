package com.sinya.projects.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.useCase.DeleteCustomExerciseUseCase
import com.sinya.projects.domain.useCase.ObserveExerciseWithSortedDataUseCase
import com.sinya.projects.domain.utils.searchByTerms
import com.sinya.projects.ui.components.sort.SortParam
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(
    private val getExerciseWithSortedDataUseCase: ObserveExerciseWithSortedDataUseCase,
    private val deleteCustomExerciseUseCase: DeleteCustomExerciseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ExercisesUiState>(ExercisesUiState.Loading)
    val state: StateFlow<ExercisesUiState> = _state.asStateFlow()

    init {
        loadData()


    }

    fun onEvent(event: ExercisesEvent) {
        when (event) {
            is ExercisesEvent.OnQueryChange -> updateIfSuccess { it.copy(query = event.s) }

            is ExercisesEvent.SortParamChange -> updateIfSuccess {
                val updatedList = it.selectedModes.map { mode ->
                    if (mode == event.mode) {
                        mode.apply(event.param as SortParam)
                    } else {
                        mode
                    }
                }
                it.copy(selectedModes = updatedList)
            }


            ExercisesEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            ExercisesEvent.ReloadData -> loadData()

            ExercisesEvent.DeleteExercises -> deleteExercise()

            is ExercisesEvent.SelectTraining -> updateIfSuccess {
                val newList = it.deleteItems.toMutableSet()
                newList.add(event.id)

                it.copy(deleteItems = newList.toList())
            }
        }
    }

    private fun deleteExercise() = viewModelScope.launch {
        val s = _state.value as? ExercisesUiState.Success ?: return@launch

        deleteCustomExerciseUseCase(s.deleteItems.firstOrNull() ?: return@launch).fold(
            onSuccess = {
                updateIfSuccess { it.copy(deleteItems = listOf()) }
            },
            onFailure = { error ->
                updateIfSuccess { it.copy(errorMessage = error.toString()) }
            }
        )
    }

    private fun loadData() = viewModelScope.launch {
        updateIfSuccess { it.copy(isRefreshing = true) }

        getExerciseWithSortedDataUseCase().collectLatest { exercises ->
            if (_state.value as? ExercisesUiState.Success != null) {
                updateIfSuccess {
                    it.copy(
                        exercises = exercises,
                        isRefreshing = false
                    )
                }
            } else {
                _state.value = ExercisesUiState.Success(
                    exercises = exercises,
                    isRefreshing = false
                )
            }
        }
    }

    fun filtered(): List<ExerciseWithMuscles> {
        val cs = _state.value as? ExercisesUiState.Success ?: return listOf()

        val filtered = cs.selectedModes.fold(cs.exercises) { exercises, mode ->
            mode.filter(exercises)
        }

        return filtered.searchByTerms(
            cs.query,
            selector = { it.name },
            isCustomSelector = { it.isCustom }
        )
    }

    private fun updateIfSuccess(transform: (ExercisesUiState.Success) -> ExercisesUiState.Success) {
        _state.update { currentState ->
            if (currentState is ExercisesUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}