package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.PlanMornings
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MorningPlanViewModel @Inject constructor(
    private val morningRepo: MorningRepository,
    private val settingsEngine: SettingsEngine
) : ViewModel() {

    private val _state = MutableStateFlow<MorningPlanUiState>(MorningPlanUiState.Loading)
    val state: StateFlow<MorningPlanUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch { observeSettings() }

            getPlans()
        }
    }

    private suspend fun observeSettings() {
        combine(
            settingsEngine.uiState
        ) { config ->
            config
        }.collectLatest { (config) ->
            val currentState = _state.value as? MorningPlanUiState.Success ?: return@collectLatest

            _state.value = currentState.copy(
                currentPlanId = config.planMorningId
            )
        }
    }

    private suspend fun getPlans() {
        _state.value = MorningPlanUiState.Success(
            items = listOf(null) + morningRepo.getPlans(),
            currentPlanId = settingsEngine.uiState.value.planMorningId
        )
    }

    fun onEvent(event: ModalSheetPlanEvent) {
        val currentState = _state.value as? MorningPlanUiState.Success ?: return

        when (event) {
            ModalSheetPlanEvent.AddPlan -> {
                viewModelScope.launch {
                    morningRepo.insertPlanMorning(
                        PlanMornings(
                            id = 0,
                            name = currentState.queryName,
                            description = currentState.queryDescription,
                        )
                    )
                    val items = morningRepo.getPlans()

                    updateIfSuccess {
                        it.copy(
                            visibleAddField = false,
                            queryName = "",
                            queryDescription = "",
                            items = listOf(null) + items
                        )
                    }
                }
            }

            ModalSheetPlanEvent.ClearQuery -> {
                updateIfSuccess {
                    it.copy(
                        queryName = "",
                        queryDescription = "",
                        visibleAddField = false,
                        visibleEditFieldId = null
                    )
                }
            }

            is ModalSheetPlanEvent.DeletePlan -> {
                viewModelScope.launch {
                    val item = currentState.items.first { it?.id == event.id }
                    item?.let { morningRepo.deletePlanMorning(item) }
                    settingsEngine.setPlanMorningId(null)
                    val items = morningRepo.getPlans()

                    updateIfSuccess {
                        it.copy(
                            visibleAddField = false,
                            visibleEditFieldId = null,
                            queryName = "",
                            queryDescription = "",
                            currentPlanId = null,
                            items = listOf(null) + items
                        )
                    }
                }
            }

            is ModalSheetPlanEvent.EditPlan -> {
                viewModelScope.launch {
                    val item =
                        currentState.items.first { it?.id == currentState.visibleEditFieldId }
                    item?.let {
                        morningRepo.updatePlanMorning(
                            item.copy(
                                name = currentState.queryName,
                                description = currentState.queryDescription
                            )
                        )
                    }
                    val items = morningRepo.getPlans()
                    updateIfSuccess {
                        it.copy(
                            visibleAddField = false,
                            visibleEditFieldId = null,
                            queryName = "",
                            queryDescription = "",
                            items = listOf(null) + items
                        )
                    }
                }
            }

            is ModalSheetPlanEvent.OnQueryDescriptionChange -> updateIfSuccess { it.copy(queryDescription = event.s) }

            is ModalSheetPlanEvent.OnQueryNameChange -> updateIfSuccess { it.copy(queryName = event.s) }

            ModalSheetPlanEvent.OpenAddPlanField -> {
                updateIfSuccess {
                    it.copy(
                        queryName = "",
                        queryDescription = "",
                        visibleAddField = true,
                        visibleEditFieldId = null
                    )
                }
            }

            is ModalSheetPlanEvent.OpenEditPlanField -> {
                updateIfSuccess {
                    val item = it.items.first { it?.id == event.id }

                    it.copy(
                        queryDescription = item?.description.toString(),
                        queryName = item?.name ?: "",
                        visibleAddField = false,
                        visibleEditFieldId = event.id
                    )
                }
            }

            is ModalSheetPlanEvent.OnPlanClick -> {
                settingsEngine.setPlanMorningId(event.id)
            }
        }
    }

    private fun updateIfSuccess(transform: (MorningPlanUiState.Success) -> MorningPlanUiState.Success) {
        _state.update { currentState ->
            if (currentState is MorningPlanUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}