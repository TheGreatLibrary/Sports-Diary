package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticScreenViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _state = mutableStateOf<StatisticScreenUiState>(StatisticScreenUiState.Loading)
    val state: State<StatisticScreenUiState> = _state

    init {
        viewModelScope.launch {
            val count = trainingRepository.getCountOfTrainings()
            val weight = trainingRepository.getSummaryWeightOfTrainings()
            val chartList = trainingRepository.getChartList(TimeMode.DAYS)

            _state.value = StatisticScreenUiState.Success(
                countTrain = count,
                countWeight = weight,
                chartList = chartList
            )
        }
    }

    fun onEvent(event: StatisticScreenUiEvent) {
        val currentState = _state.value as? StatisticScreenUiState.Success ?: return

        when(event) {
            is StatisticScreenUiEvent.OnSelectTimePeriod -> {
                viewModelScope.launch {
                    val timeMode = TimeMode.fromIndex(event.index)
                    val chartList = trainingRepository.getChartList(timeMode)

                    _state.value = currentState.copy(
                        timeMode = timeMode,
                        chartList = chartList
                    )
                }

            }
        }
    }


}