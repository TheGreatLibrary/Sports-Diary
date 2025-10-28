package com.sinya.projects.sportsdiary.presentation.exercises

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExercisesRepository
) : ViewModel() {

    private val _state = mutableStateOf<ExercisesUiState>(ExercisesUiState.Loading)
    val state: State<ExercisesUiState> = _state

    init {
        viewModelScope.launch {
            val list = exerciseRepository.getExerciseTranslations(Locale.current.language)

            _state.value = ExercisesUiState.Success(
                exercises = list,
            )
        }
    }

    fun onEvent(event: ExercisesEvent) {

    }
}