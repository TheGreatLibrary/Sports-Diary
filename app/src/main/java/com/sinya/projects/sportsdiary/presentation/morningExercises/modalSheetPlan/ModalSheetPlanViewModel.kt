package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.database.entity.PlanMornings
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote.ModalSheetNoteEvent
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining.TrainingBottomSheetTrainingUiEvent
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining.TrainingBottomSheetTrainingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    fun onEvent(event: ModalSheetPlanEvent) {
        val currentState = _state.value as? ModalSheetPlanUiState.Success ?: return

        when(event) {
            ModalSheetPlanEvent.AddPlan -> {
                viewModelScope.launch {
                    morningRepo.insertPlanMorning(
                        PlanMornings(
                            id = 0,
                            name = currentState.queryName,
                            description = currentState.queryDescription,
                        )
                    )
                    _state.value = currentState.copy(
                        visibleAddField = false,
                        queryName = "",
                        queryDescription = "",
                        items = morningRepo.getPlans()
                    )
                }
            }
            ModalSheetPlanEvent.ClearQuery -> {
                _state.value = currentState.copy(
                    queryName = "",
                    queryDescription = "",
                    visibleAddField = false,
                    visibleEditFieldId = null
                )
            }
            is ModalSheetPlanEvent.DeletePlan -> {
                viewModelScope.launch {
                    val item = currentState.items.first { it.id == event.id }
                    morningRepo.deletePlanMorning(item)

                    _state.value = currentState.copy(
                        visibleAddField = false,
                        visibleEditFieldId = null,
                        queryName = "",
                        queryDescription = "",
                        items = morningRepo.getPlans()
                    )
                }
            }
            is ModalSheetPlanEvent.EditPlan -> {
                viewModelScope.launch {
                    val item = currentState.items.first { it.id == currentState.visibleEditFieldId }
                    morningRepo.updatePlanMorning(
                        item.copy(
                            name = currentState.queryName,
                            description = currentState.queryDescription
                        )
                    )
                    _state.value = currentState.copy(
                        visibleAddField = false,
                        visibleEditFieldId = null,
                        queryName = "",
                        queryDescription = "",
                        items = morningRepo.getPlans()
                    )
                }
            }
            is ModalSheetPlanEvent.OnQueryDescriptionChange -> {
                _state.value = currentState.copy(
                    queryDescription = event.s
                )
            }
            is ModalSheetPlanEvent.OnQueryNameChange -> {
                _state.value = currentState.copy(
                    queryName = event.s
                )
            }
            ModalSheetPlanEvent.OpenAddPlanField -> {
                _state.value = currentState.copy(
                    queryName = "",
                    queryDescription = "",
                    visibleAddField = true,
                    visibleEditFieldId = null
                )
            }
            is ModalSheetPlanEvent.OpenEditPlanField -> {
                val item = currentState.items.first { it.id == event.id }
                _state.value = currentState.copy(
                    queryDescription = item.description.toString(),
                    queryName = item.name,
                    visibleAddField = false,
                    visibleEditFieldId = event.id
                )
            }
        }
    }

}