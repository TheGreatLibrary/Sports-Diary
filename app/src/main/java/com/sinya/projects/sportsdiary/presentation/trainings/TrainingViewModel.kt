package com.sinya.projects.sportsdiary.presentation.trainings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repo: TrainingRepository,
) : ViewModel() {

    private val _state = mutableStateOf<TrainingUiState>(TrainingUiState.Loading)
    val state: State<TrainingUiState> = _state

    init {
        loadData()
    }

    fun onEvent(event: TrainingEvent) {
        val currentState = _state.value as? TrainingUiState.Success ?: return

        when(event) {
            is TrainingEvent.ModeChange -> {
                _state.value = currentState.copy(
                    mode = event.mode
                )
            }
            is TrainingEvent.ReloadData -> {
                viewModelScope.launch {
                    _state.value = TrainingUiState.Success(
                        trainings = repo.trainingList()
                    )
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = TrainingUiState.Success(
                    trainings = repo.trainingList()
                )
            }
            catch (e: Exception) {
                _state.value = TrainingUiState.Error(
                    message = "Ошибка загрузки данных: ${e.message}"
                )
            }
        }
    }
}