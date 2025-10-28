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
class StatisticViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _state = mutableStateOf<StatisticUiState>(StatisticUiState.Loading)
    val state: State<StatisticUiState> = _state

    init {
        viewModelScope.launch {
            val count = trainingRepository.getCountOfTrainings()
            val weight = trainingRepository.getSummaryWeightOfTrainings()
            val chartList = trainingRepository.getChartList(TimeMode.DAYS)

            _state.value = StatisticUiState.Success(
                countTrain = count,
                countWeight = weight,
                chartList = chartList
            )
        }
    }

    fun onEvent(event: StatisticEvent) {
        val currentState = _state.value as? StatisticUiState.Success ?: return

        when (event) {
            is StatisticEvent.OnSelectTimePeriod -> {
                viewModelScope.launch {
                    val timeMode = TimeMode.fromIndex(event.index)
                    val chartList = trainingRepository.getChartList(timeMode)

                    _state.value = currentState.copy(
                        timeMode = timeMode,
                        chartList = chartList
                    )
                }

            }

            is StatisticEvent.OnDialogState -> {
                _state.value = currentState.copy(
                    dialogState = event.state
                )
            }
        }
    }
}