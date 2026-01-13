package com.sinya.projects.sportsdiary.presentation.exercisePage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ExercisePageViewModel @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {

    private val _state = mutableStateOf<ExercisePageUiState>(ExercisePageUiState.Loading)
    val state: State<ExercisePageUiState> = _state

    fun init(id: Int) {
        val locale = Locale.current.language

        viewModelScope.launch {
            val item = exercisesRepository.getExerciseById(id, locale).getOrThrow()
            val exMuscles = exercisesRepository.getExerciseMusclesById(id, locale)

            _state.value = ExercisePageUiState.Success(
                exercise = item,
                exMuscles = exMuscles
            )
        }
    }

    fun onEvent(event: ExercisePageEvent) {

    }
}