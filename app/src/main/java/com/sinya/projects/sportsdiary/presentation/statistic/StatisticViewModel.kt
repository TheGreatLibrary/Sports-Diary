package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.core.domain.useCase.GetCountTrainingUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetSummaryWeightTrainingUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetTrainingChartUseCase
import com.sinya.projects.sportsdiary.core.utils.getErrorMessage
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
    private val getTrainingChartUseCase: GetTrainingChartUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<StatisticUiState>(StatisticUiState.Loading)
    val state: StateFlow<StatisticUiState> = _state.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll(type: TypeTime = TypeTime.DAYS) = viewModelScope.launch{
        val countResult = getCountTrainingUseCase()
        val weightResult = getSummaryWeightTrainingUseCase()
        val chartResult = getTrainingChartUseCase(type)

        val error = listOf(countResult, weightResult, chartResult)
            .firstOrNull { it.isFailure }
            ?.exceptionOrNull()

        if (error != null) {
            _state.update {
                StatisticUiState.Success(errorMessage = error.getErrorMessage())
            }
            return@launch
        }

        _state.update {
            StatisticUiState.Success(
                countTrain = countResult.getOrThrow(),
                countWeight = weightResult.getOrThrow(),
                timeMode = type,
                chartList = chartResult.getOrThrow(),
            )
        }
    }

    fun onEvent(event: StatisticEvent) {
        when (event) {
            is StatisticEvent.OnSelectTimePeriod -> loadChart(type = event.mode)

            is StatisticEvent.OnMessage -> updateIfSuccess {
                it.copy(errorMessage = event.message)
            }
        }
    }
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
            onFailure = { error -> handleError(error) }
        )
    }

    private fun handleError(error: Throwable) {
        updateIfSuccess {
            it.copy(errorMessage = error.getErrorMessage())
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