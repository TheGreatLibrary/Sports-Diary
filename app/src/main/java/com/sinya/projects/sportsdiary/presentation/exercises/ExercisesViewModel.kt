package com.sinya.projects.sportsdiary.presentation.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.domain.model.SortParam
import com.sinya.projects.sportsdiary.domain.useCase.DeleteCustomExerciseUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseWithSortedDataUseCase
import com.sinya.projects.sportsdiary.utils.searchByTerms
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val getExerciseWithSortedDataUseCase: GetExerciseWithSortedDataUseCase,
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

            is ExercisesEvent.OpenDialog -> updateIfSuccess { it.copy(deleteDialogId = event.id) }

            ExercisesEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            ExercisesEvent.ReloadData -> loadData()

            ExercisesEvent.Delete -> deleteExercise()
        }
    }

    private fun deleteExercise() = viewModelScope.launch {
        val s = _state.value as? ExercisesUiState.Success ?: return@launch

        deleteCustomExerciseUseCase(s.deleteDialogId ?: return@launch).fold(
            onSuccess = {
                updateIfSuccess { it.copy(deleteDialogId = null) }
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