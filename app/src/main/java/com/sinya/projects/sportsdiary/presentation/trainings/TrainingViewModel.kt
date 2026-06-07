package com.sinya.projects.sportsdiary.presentation.trainings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Trainings
import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.core.domain.useCase.DeleteTrainingUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetTrainingListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val deleteTrainingUseCase: DeleteTrainingUseCase,
    private val getTrainingUseCase: GetTrainingListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val state: StateFlow<TrainingUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: TrainingEvent) {
        when (event) {
            is TrainingEvent.ModeChange -> {
                updateIfSuccess {
                    it.copy(selectedMode = ModeOfSorting.fromCode(event.code))
                }
            }

            is TrainingEvent.SortParamChange -> {
                updateIfSuccess {
                    it.copy(
                        selectedMode = it.selectedMode.apply(event.param)
                    )
                }
            }

            is TrainingEvent.OpenDialog -> updateIfSuccess { it.copy(deleteDialogId = event.id) }

            TrainingEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            TrainingEvent.ReloadData -> loadData()

            TrainingEvent.DeleteTraining -> delete()
        }
    }

    private fun delete() = viewModelScope.launch {
        val currentState = _state.value as? TrainingUiState.Success ?: return@launch

        currentState.deleteDialogId?.let { id ->
            val item: Trainings = currentState.trainings.first { id == it.id }.let { item ->
                Trainings(
                    id = item.id,
                    typeId = item.categoryId,
                    serialNum = item.name.toInt(),
                    date = item.date
                )
            }

            deleteTrainingUseCase(item).fold(
                onSuccess = {
                    updateIfSuccess { it.copy(deleteDialogId = null) }
                },
                onFailure = { error ->
                    updateIfSuccess { it.copy(errorMessage = error.toString()) }
                }
            )
        }
    }

    private fun loadData() = viewModelScope.launch {
        updateIfSuccess { it.copy(isRefreshing = true) }

        getTrainingUseCase().collectLatest { list ->
            if (_state.value is TrainingUiState.Success) {
                updateIfSuccess {
                    it.copy(
                        trainings = list,
                        isRefreshing = false
                    )
                }
            }
            else {
                _state.value = TrainingUiState.Success(trainings = list, isRefreshing = false)
            }
        }
    }

    private fun updateIfSuccess(transform: (TrainingUiState.Success) -> TrainingUiState.Success) {
        _state.update { currentState ->
            if (currentState is TrainingUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}