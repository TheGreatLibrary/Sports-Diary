package com.sinya.projects.sportsdiary.presentation.exercisePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseMusclesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ExercisePageViewModel.Factory::class)
class ExercisePageViewModel @AssistedInject constructor(
    @Assisted("id") val id: Int?,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,
    private val getExerciseMusclesUseCase: GetExerciseMusclesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ExercisePageUiState>(ExercisePageUiState.Loading)
    val state: StateFlow<ExercisePageUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): ExercisePageViewModel
    }

    init {
        if (id != null) loadSuccess(id)
    }

    private fun loadSuccess(id: Int) = viewModelScope.launch {
        combine(
            getExerciseDescriptionUseCase(id),
            getExerciseMusclesUseCase(id)
        ) { exercise, muscles ->
            ExercisePageUiState.Success(
                exercise = exercise,
                exMuscles = muscles
            )
        }
        .catch { error ->
            _state.value = ExercisePageUiState.Error(error.message ?: "Unknown error")
        }
        .collect { state ->
            _state.value = state
        }
    }

}