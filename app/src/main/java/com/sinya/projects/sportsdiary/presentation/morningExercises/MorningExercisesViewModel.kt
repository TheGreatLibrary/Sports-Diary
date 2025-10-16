package com.sinya.projects.sportsdiary.presentation.morningExercises

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class MorningExercisesViewModel @Inject constructor(

) : ViewModel() {

    private val _state = mutableStateOf<MorningExercisesUiState>(MorningExercisesUiState.Loading)
    val state: State<MorningExercisesUiState> = _state

    init {
       _state.value = MorningExercisesUiState.Success(
           countTraining = 10,
           seriesScope = 12,
           listPlan = listOf("бла бла бла", "блаблабла"),
           listNote = listOf("ОГО ОГО", "гыгы гыгыыгыыг")
       )
    }

    fun onEvent(event: MorningExercisesUiEvent) {
        when(event) {
          else -> Unit
        }
    }
}