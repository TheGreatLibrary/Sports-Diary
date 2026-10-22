package com.sinya.projects.trainings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.ui.components.sort.ModeOfSorting
import com.sinya.projects.domain.model.TrainingsEntry
import com.sinya.projects.domain.useCase.DeleteTrainingsUseCase
import com.sinya.projects.domain.useCase.GetTrainingListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class TrainingViewModel @Inject constructor(
    private val deleteTrainingUseCase: DeleteTrainingsUseCase,
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
                    it.copy(selectedMode = it.selectedMode.apply(event.param))
                }
            }



            TrainingEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            TrainingEvent.ReloadData -> loadData()

            TrainingEvent.DeleteTraining -> delete()

            TrainingEvent.ClearedListToDelete -> updateIfSuccess {
                it.copy(
                    deleteItems = listOf(),
                    trainings = it.trainings.map { it.copy(selected = false) })
            }

            is TrainingEvent.SelectTraining -> updateIfSuccess {
                val newList = it.deleteItems.toMutableSet()

                val selected = !it.trainings.first { it.id == event.id }.selected

                if (selected) newList.add(event.id) else newList.remove(event.id)
                it.copy(
                    deleteItems = newList.toList(),
                    trainings = it.trainings.map {
                        if (it.id == event.id) it.copy(selected = selected) else it
                    }
                )
            }
        }
    }

    private fun delete() {
        val currentState = _state.value as? TrainingUiState.Success ?: return

        if (currentState.deleteItems.isNotEmpty()) {
            val items: List<TrainingsEntry> = currentState.trainings.filter {
                currentState.deleteItems.contains(it.id)
            }.let { list ->
                list.map { item ->
                    TrainingsEntry(
                        id = item.id,
                        typeId = item.categoryId,
                        serialNum = item.name.toInt(),
                        date = item.date
                    )
                }
            }

            viewModelScope.launch {
                deleteTrainingUseCase(items).fold(
                    onSuccess = {
                        updateIfSuccess { it.copy(deleteItems = listOf()) }
                    },
                    onFailure = { error ->
                        updateIfSuccess { it.copy(errorMessage = error.toString()) }
                    }
                )
            }
        }
    }

    private fun loadData() {
        updateIfSuccess { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            getTrainingUseCase().collectLatest { list ->
                if (_state.value is TrainingUiState.Success) {
                    updateIfSuccess {
                        it.copy(
                            trainings = list,
                            isRefreshing = false
                        )
                    }
                } else {
                    _state.update {
                        TrainingUiState.Success(trainings = list, isRefreshing = false)
                    }
                }
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