package com.sinya.projects.sportsdiary.presentation.sportExercises

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SportExercisesViewModel @Inject constructor(
    val exerciseRepository: ExercisesRepository
) : ViewModel() {

    private val _state = mutableStateOf<SportExercisesUiState>(SportExercisesUiState.Loading)
    val state: State<SportExercisesUiState> = _state

    init {
        viewModelScope.launch {
            val list = exerciseRepository.getExerciseTranslations()

            _state.value = SportExercisesUiState.Success(
                exercises = list
            )
        }
    }

    fun onEvent(event: SportExercisesUiEvent) {

    }
}