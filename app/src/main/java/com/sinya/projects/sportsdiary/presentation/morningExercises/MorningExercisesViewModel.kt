package com.sinya.projects.sportsdiary.presentation.morningExercises

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MorningExercisesViewModel @Inject constructor(
    private val morningRepo: MorningRepository
) : ViewModel() {

    private val _state = mutableStateOf<MorningExercisesUiState>(MorningExercisesUiState.Loading)
    val state: State<MorningExercisesUiState> = _state

    init {
        viewModelScope.launch {
            val count = morningRepo.getCount()

            _state.value = MorningExercisesUiState.Success(
                countTraining = count,
                seriesScope = 0
            )
        }
    }

    fun onEvent(event: MorningExercisesUiEvent) {
        val currentState = _state.value as? MorningExercisesUiState.Success ?: return

        when(event) {
            is MorningExercisesUiEvent.OnNoteExpanded -> {
                _state.value = currentState.copy(
                    noteExpanded = event.state
                )
            }
            is MorningExercisesUiEvent.OnPlanExpanded -> {
                _state.value = currentState.copy(
                    planExpanded = event.state
                )
            }
        }
    }
}