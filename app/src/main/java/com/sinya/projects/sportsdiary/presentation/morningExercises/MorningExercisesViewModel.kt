package com.sinya.projects.sportsdiary.presentation.morningExercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MorningExercisesViewModel @Inject constructor(
    private val morningRepo: MorningRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MorningExercisesUiState>(MorningExercisesUiState.Loading)
    val state: StateFlow<MorningExercisesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val count = morningRepo.getCount()
            val series = morningRepo.getSeriesScopeMorning()

            _state.update {
                MorningExercisesUiState.Success(
                    countTraining = count,
                    seriesScope = series
                )
            }
        }
    }

    fun onEvent(event: MorningExercisesEvent) {
        when(event) {
            is MorningExercisesEvent.OnNoteExpanded -> updateIfSuccess { it.copy(noteExpanded = event.state) }
            is MorningExercisesEvent.OnPlanExpanded -> updateIfSuccess { it.copy(planExpanded = event.state) }
        }
    }

    private fun updateIfSuccess(transform: (MorningExercisesUiState.Success) -> MorningExercisesUiState.Success) {
        _state.update { currentState ->
            if (currentState is MorningExercisesUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}