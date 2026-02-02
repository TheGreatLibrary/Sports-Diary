package com.sinya.projects.sportsdiary.presentation.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.domain.model.SortParam
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseWithSortedDataUseCase
import com.sinya.projects.sportsdiary.utils.searchByTerms
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val getExerciseWithSortedDataUseCase: GetExerciseWithSortedDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ExercisesUiState>(ExercisesUiState.Loading)
    val state: StateFlow<ExercisesUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: ExercisesEvent) {
        val currentState = _state.value as? ExercisesUiState.Success ?: return

        when(event) {
            is ExercisesEvent.OnQueryChange -> {
                _state.value = currentState.copy(
                    query = event.s
                )
            }

            is ExercisesEvent.SortParamChange -> {
                updateIfSuccess {
                    val updatedList = it.selectedModes.map { mode ->
                        if (mode == event.mode) {
                            mode.apply(event.param as SortParam)
                        } else {
                            mode
                        }
                    }
                    it.copy(selectedModes = updatedList)
                }
            }

            ExercisesEvent.OnErrorShown -> updateIfSuccess {
                it.copy(errorMessage = null)
            }

            ExercisesEvent.ReloadData -> { }
        }
    }

    private fun loadData() = viewModelScope.launch {
        getExerciseWithSortedDataUseCase().fold(
            onSuccess = { list ->
                _state.value = ExercisesUiState.Success(
                    exercises = list
                )
            },
            onFailure = { error ->
                _state.value = ExercisesUiState.Success(
                    exercises = listOf(),
                    errorMessage = error.toString()
                )
            }
        )
    }

    fun filtered() : List<ExerciseWithMuscles> {
        val cs = _state.value as? ExercisesUiState.Success ?: return listOf()

        val filtered = cs.selectedModes.fold(cs.exercises) { exercises, mode ->
            mode.filter(exercises)
        }

        return filtered.searchByTerms(cs.query) { it.name }
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