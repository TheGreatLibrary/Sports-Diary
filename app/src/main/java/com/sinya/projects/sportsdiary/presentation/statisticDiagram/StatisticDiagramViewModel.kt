package com.sinya.projects.sportsdiary.presentation.statisticDiagram

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExercisesDataUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExercisesUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetMeasureDataUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetMeasurementUseCase
import com.sinya.projects.sportsdiary.core.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticDiagramViewModel @Inject constructor(
    private val getMeasureDataUseCase: GetMeasureDataUseCase,
    private val getMeasurementsUseCase: GetMeasurementUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getExerciseDataUseCase: GetExercisesDataUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<StatisticDiagramUiState>(StatisticDiagramUiState.Loading)
    val state: StateFlow<StatisticDiagramUiState> = _state.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() = viewModelScope.launch{
        val measurements = getMeasurementsUseCase()
        val exercises = getExercisesUseCase()

        val error = listOf(measurements, exercises)
            .firstOrNull { it.isFailure }
            ?.exceptionOrNull()

        if (error != null) {
            _state.update {
                StatisticDiagramUiState.Success(errorMessage = error.getErrorMessage())
            }
            return@launch
        }

        _state.update {
            StatisticDiagramUiState.Success(
                measurements = measurements.getOrThrow(),
                exercises = exercises.getOrThrow()
            )
        }
    }

    fun onEvent(event: StatisticDiagramEvent) {
        when (event) {
            is StatisticDiagramEvent.OnMessage -> updateIfSuccess {
                it.copy(errorMessage = event.error)
            }

            is StatisticDiagramEvent.OnToggleMeasure -> {
                updateIfSuccess {
                    it.copy(
                        measurements = it.measurements.map { item ->
                            if (event.id == item.id) {
                                item.copy(checked = !item.checked)
                            } else item
                        }
                    )
                }
                viewModelScope.launch {
                    loadMeasureData()
                }
            }

            is StatisticDiagramEvent.OnToggleExercise -> {
                updateIfSuccess {
                    it.copy(
                        exercises = it.exercises.map { item ->
                            if (event.id == item.id) {
                                item.copy(checked = !item.checked)
                            } else item
                        }
                    )
                }
                viewModelScope.launch {
                    loadExercisesData()
                }
            }
        }
    }

    private suspend fun loadMeasureData()  {
        val state = _state.value as? StatisticDiagramUiState.Success ?: return

        getMeasureDataUseCase(state.measurements.filter { it.checked }.map { it.id }).fold(
            onSuccess = { chart ->
                updateIfSuccess { it ->
                    it.copy(
                        measurementsData = chart.map { item ->
                            item.copy(
                                label = state.measurements.first { it.id == item.id }.title
                            )
                        }
                    )
                }
            },
            onFailure = { error -> handleError(error) }
        )
    }
    private suspend fun loadExercisesData()  {
        val state = _state.value as? StatisticDiagramUiState.Success ?: return

        getExerciseDataUseCase(state.exercises.filter { it.checked }.map { it.id }).fold(
            onSuccess = { chart ->
                updateIfSuccess { it ->
                    it.copy(
                        exercisesData = chart.map { item ->
                            item.copy(
                                label = state.exercises.first { it.id == item.id }.title
                            )
                        }
                    )
                }
            },
            onFailure = { error -> handleError(error) }
        )
    }

    private fun handleError(error: Throwable) {
        updateIfSuccess {
            it.copy(errorMessage = error.getErrorMessage())
        }
    }

    private fun updateIfSuccess(transform: (StatisticDiagramUiState.Success) -> StatisticDiagramUiState.Success) {
        _state.update { currentState ->
            if (currentState is StatisticDiagramUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}
