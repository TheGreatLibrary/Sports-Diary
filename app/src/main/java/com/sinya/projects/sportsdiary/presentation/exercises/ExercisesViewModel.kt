package com.sinya.projects.sportsdiary.presentation.exercises

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises.TrainingExerciseEvent
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises.TrainingExerciseUiState
import com.sinya.projects.sportsdiary.utils.searchByTerms
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
                exercises = list
            )
        }
    }

    fun onEvent(event: ExercisesEvent) {
        val currentState = _state.value as? ExercisesUiState.Success ?: return

        when(event) {
            is ExercisesEvent.OnQueryChange -> {
                _state.value = currentState.copy(
                    query = event.s
                )
            }
        }
    }

    fun filtered() : List<ExerciseTranslations> {
        val currentState = _state.value as? ExercisesUiState.Success ?: return listOf()
        return currentState.exercises.searchByTerms(currentState.query) { it.name }
    }
}