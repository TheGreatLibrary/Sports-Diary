package com.sinya.projects.sportsdiary.presentation.sportExercisePage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.presentation.sportExercises.SportExercisesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SportExercisePageViewModel @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {

    private val _state = mutableStateOf<SportExercisePageUiState>(SportExercisePageUiState.Loading)
    val state: State<SportExercisePageUiState> = _state

    fun init(id: Int) {
        viewModelScope.launch {
            val item = exercisesRepository.getExerciseById(id)

            _state.value = SportExercisePageUiState.Success(
                exercise = item
            )
        }
    }

    fun onEvent(event: SportExercisePageUiEvent) {

    }
}