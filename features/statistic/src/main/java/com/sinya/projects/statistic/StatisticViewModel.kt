package com.sinya.projects.statistic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.enums.TypeTime
import com.sinya.projects.domain.providers.MapStateProvider
import com.sinya.projects.domain.useCase.GetCountMorningTrainingUseCase
import com.sinya.projects.domain.useCase.GetCountTrainingUseCase
import com.sinya.projects.domain.useCase.GetDataExerciseMusclesUseCase
import com.sinya.projects.domain.useCase.GetMusclesUseCase
import com.sinya.projects.domain.useCase.GetSeriesScopeMorningUseCase
import com.sinya.projects.domain.useCase.GetSummaryWeightTrainingUseCase
import com.sinya.projects.domain.useCase.GetTrainingChartUseCase
import com.sinya.projects.domain.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
internal class StatisticViewModel @Inject constructor(
    private val getCountTrainingUseCase: GetCountTrainingUseCase,
    private val getCountMorningTrainingUseCase: GetCountMorningTrainingUseCase,
    private val getSeriesScopeMorningUseCase: GetSeriesScopeMorningUseCase,
    private val getSummaryWeightTrainingUseCase: GetSummaryWeightTrainingUseCase,
    private val getTrainingChartUseCase: GetTrainingChartUseCase,
    private val getDataExerciseMusclesUseCase: GetDataExerciseMusclesUseCase,
    private val getMusclesUseCase: GetMusclesUseCase,
    private val mapStateProvider: MapStateProvider
) : ViewModel() {

    private val _state = MutableStateFlow<StatisticUiState>(StatisticUiState.Loading)
    val state: StateFlow<StatisticUiState> = _state.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll(type: TypeTime = TypeTime.DAYS) = viewModelScope.launch {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(6)
        val mapState = mapStateProvider.currentMapState.first()

        val countTrainingsDeferred = async { getCountTrainingUseCase() }
        val allMusclesDeferred = async { getMusclesUseCase() }
        val countMorningDeferred = async { getCountMorningTrainingUseCase() }
        val seriesScopeDeferred = async { getSeriesScopeMorningUseCase() }
        val weightSummaryDeferred = async { getSummaryWeightTrainingUseCase() }
        val chartResultDeferred = async { getTrainingChartUseCase(type) }
        val exerciseMusclesDeferred = async {
            getDataExerciseMusclesUseCase(
                startDate.toString(),
                endDate.toString()
            )
        }

        val countTrainings = countTrainingsDeferred.await()
        val allMuscles = allMusclesDeferred.await()
        val countMorningTrainings = countMorningDeferred.await()
        val seriesScope = seriesScopeDeferred.await()
        val weightSummary = weightSummaryDeferred.await()
        val chartResult = chartResultDeferred.await()
        val exerciseMuscles = exerciseMusclesDeferred.await()

        val error = listOf(
            countTrainings,
            allMuscles,
            countMorningTrainings,
            seriesScope,
            weightSummary,
            chartResult,
            exerciseMuscles
        ).firstOrNull { it.isFailure }?.exceptionOrNull()

        if (error != null) {
            Log.e("CharT", error.message, error)
        }

        _state.update {
            StatisticUiState.Success(
                mapState = mapState,
                countTrain = countTrainings.getOrElse { 0 },
                countWeight = weightSummary.getOrElse { 0f },
                countMorning = countMorningTrainings.getOrElse { 0 },
                seriesScope = seriesScope.getOrElse { 0 },
                chartList = chartResult.getOrElse { emptyList() },
                dataExerciseMuscles = exerciseMuscles.getOrElse { emptyList() },
                allMuscles = allMuscles.getOrElse { emptyList() }.map { it.muscleId to it.name },
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

