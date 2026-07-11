package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.core.domain.useCase.GetCountMorningTrainingUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetCountTrainingUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetDataExerciseMusclesUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetMusclesUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetSeriesScopeMorningUseCase
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
import java.time.LocalDate

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getCountTrainingUseCase: GetCountTrainingUseCase,
    private val getCountMorningTrainingUseCase: GetCountMorningTrainingUseCase,
    private val getSeriesScopeMorningUseCase: GetSeriesScopeMorningUseCase,
    private val getSummaryWeightTrainingUseCase: GetSummaryWeightTrainingUseCase,
    private val getTrainingChartUseCase: GetTrainingChartUseCase,
    private val getDataExerciseMusclesUseCase: GetDataExerciseMusclesUseCase,
    private val getMusclesUseCase: GetMusclesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<StatisticUiState>(StatisticUiState.Loading)
    val state: StateFlow<StatisticUiState> = _state.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll(type: TypeTime = TypeTime.DAYS) = viewModelScope.launch {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(6)

        val countTrainings = getCountTrainingUseCase()
        val allMuscles = getMusclesUseCase()
        val countMorningTrainings = getCountMorningTrainingUseCase()
        val seriesScope = getSeriesScopeMorningUseCase()
        val weightSummary = getSummaryWeightTrainingUseCase()
        val chartResult = getTrainingChartUseCase(type)
        val exerciseMuscles = getDataExerciseMusclesUseCase(
            startDate.toString(),
            endDate.toString()
        )

        val error = listOf(countTrainings, weightSummary, chartResult)
            .firstOrNull { it.isFailure }
            ?.exceptionOrNull()

        _state.update {
            StatisticUiState.Success(
                countTrain = countTrainings.getOrElse { 0 },
                countWeight = weightSummary.getOrElse { 0f },
                countMorning = countMorningTrainings.getOrElse { 0 },
                seriesScope = seriesScope.getOrElse { 0 },
                chartList = chartResult.getOrElse { emptyList() },
                dataExerciseMuscles = exerciseMuscles.getOrElse { emptyList() },
                allMuscles = allMuscles.getOrElse { emptyList() }.map { Pair(it.muscleId, it.name) },
                errorMessage = error?.getErrorMessage(),
                timeMode = type
            )
        }
    }

    fun onEvent(event: StatisticEvent) {
        when (event) {
            is StatisticEvent.OnSelectTimePeriod -> loadChart(type = event.mode)

            is StatisticEvent.OnMessage -> updateIfSuccess {
                it.copy(errorMessage = event.message)
            }

            is StatisticEvent.OnBodyInfoClick -> updateIfSuccess {
                it.copy(bodyVisible = event.visible)
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