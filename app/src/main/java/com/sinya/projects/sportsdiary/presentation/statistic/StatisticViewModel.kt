package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.domain.useCase.GetCountTrainingUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetSummaryWeightTrainingUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetTrainingChartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getCountTrainingUseCase: GetCountTrainingUseCase,
    private val getSummaryWeightTrainingUseCase: GetSummaryWeightTrainingUseCase,
    private val getTrainingChartUseCase: GetTrainingChartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<StatisticUiState>(StatisticUiState.Loading)
    val state: StateFlow<StatisticUiState> = _state.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll(type: TypeTime = TypeTime.DAYS) = viewModelScope.launch {
        _state.value = StatisticUiState.Loading

        val countResult = getCountTrainingUseCase()
        val weightResult = getSummaryWeightTrainingUseCase()
        val chartResult = getTrainingChartUseCase(type)

        val error = listOf(countResult, weightResult, chartResult)
            .firstOrNull { it.isFailure }
            ?.exceptionOrNull()

        if (error != null) {
            _state.value = StatisticUiState.Success(errorMessage = error.toString())
            return@launch
        }

        _state.value = StatisticUiState.Success(
            countTrain = countResult.getOrThrow(),
            countWeight = weightResult.getOrThrow(),
            chartList = chartResult.getOrThrow(),
            timeMode = type
        )
    }

//    private fun loadCountTraining() = viewModelScope.launch {
//        getCountTrainingUseCase().fold(
//            onSuccess = { count ->
//                if (_state.value is StatisticUiState.Success) {
//                    updateIfSuccess { it.copy(countTrain = count) }
//                }
//                else _state.value = StatisticUiState.Success(countTrain = count)
//            },
//            onFailure = { error ->
//                if (_state.value is StatisticUiState.Success) {
//                    updateIfSuccess { it.copy(errorMessage = error.toString()) }
//                }
//                else _state.value = StatisticUiState.Success(errorMessage = error.toString())
//            }
//        )
//    }
//
//    private fun loadWeightTraining() = viewModelScope.launch {
//        getSummaryWeightTrainingUseCase().fold(
//            onSuccess = { weight ->
//                if (_state.value is StatisticUiState.Success) {
//                    updateIfSuccess { it.copy(countWeight = weight) }
//                }
//                else _state.value = StatisticUiState.Success(countWeight = weight)
//            },
//            onFailure = { error ->
//                if (_state.value is StatisticUiState.Success) {
//                    updateIfSuccess { it.copy(errorMessage = error.toString()) }
//                }
//                else _state.value = StatisticUiState.Success(errorMessage = error.toString())
//            }
//        )
//    }

    private fun loadChart(type: TypeTime = TypeTime.DAYS) = viewModelScope.launch {
        getTrainingChartUseCase(type).fold(
            onSuccess = { chart ->
                updateIfSuccess {
                    it.copy(
                        timeMode = type,
                        chartList = chart
                    )
                }
            },
            onFailure = { error ->
                updateIfSuccess { it.copy(errorMessage = error.toString()) }
            }
        )
    }

    fun onEvent(event: StatisticEvent) {
         when (event) {
            is StatisticEvent.OnSelectTimePeriod -> loadChart(type = TypeTime.fromIndex(event.index))

            is StatisticEvent.OnDialogState -> updateIfSuccess {
                it.copy(dialogState = event.state)
            }

             StatisticEvent.OnErrorShown -> updateIfSuccess {
                 it.copy(errorMessage = null)
             }
         }
    }

    private fun updateIfSuccess(transform: (StatisticUiState.Success) -> StatisticUiState.Success) {
        _state.update { currentState ->
            if (currentState is StatisticUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}