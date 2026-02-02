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
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ExercisePageViewModel.Factory::class)
class ExercisePageViewModel @AssistedInject constructor(
    @Assisted("id") val id: Int,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,
    private val getExerciseMusclesUseCase: GetExerciseMusclesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ExercisePageUiState>(ExercisePageUiState.Loading)
    val state: StateFlow<ExercisePageUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int
        ): ExercisePageViewModel
    }

    init {
        viewModelScope.launch {
            val exercise = getExerciseDescriptionUseCase(id)
            val exMuscles = getExerciseMusclesUseCase(id)

            val error = listOf(exercise, exMuscles)
                .firstOrNull { it.isFailure }
                ?.exceptionOrNull()

            if (error!=null)  _state.value = ExercisePageUiState.Error(error.toString())

            _state.value = ExercisePageUiState.Success(
                exercise = exercise.getOrThrow(),
                exMuscles = exMuscles.getOrThrow()
            )
        }
    }

    fun onEvent(event: ExercisePageEvent) { }
}