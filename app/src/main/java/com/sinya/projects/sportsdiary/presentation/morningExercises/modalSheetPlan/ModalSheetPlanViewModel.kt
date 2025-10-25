package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining.TrainingBottomSheetTrainingUiEvent
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining.TrainingBottomSheetTrainingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ModalSheetPlanViewModel @Inject constructor(
    private val morningRepo: MorningRepository
) : ViewModel() {

    private val _state = mutableStateOf<ModalSheetPlanUiState>(ModalSheetPlanUiState.Loading)
    val state: State<ModalSheetPlanUiState> = _state

    init {
        viewModelScope.launch {
            _state.value = ModalSheetPlanUiState.Success(
                items = morningRepo.getPlans()
            )
        }
    }

    fun onEvent(event: TrainingBottomSheetTrainingUiEvent) {
        val currentState = _state.value as? TrainingBottomSheetTrainingUiState.Success ?: return

        when(event) {
            is TrainingBottomSheetTrainingUiEvent.AddTrainings -> TODO()
            is TrainingBottomSheetTrainingUiEvent.OnQueryChange -> TODO()
            is TrainingBottomSheetTrainingUiEvent.SetAll -> TODO()
            is TrainingBottomSheetTrainingUiEvent.Toggle -> TODO()
            TrainingBottomSheetTrainingUiEvent.ToggleExpanded -> TODO()
        }
    }

}