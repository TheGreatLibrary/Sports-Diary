package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataMorning
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class MorningNoteViewModel @Inject constructor(
    private val morningRepo: MorningRepository,
    private val settingsEngine: SettingsEngine
) : ViewModel() {

    private val _state = MutableStateFlow<MorningNoteUiState>(MorningNoteUiState.Loading)
    val state: StateFlow<MorningNoteUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                MorningNoteUiState.Success(
                    items = morningRepo.getNotes(),
                    currentPlanId = settingsEngine.uiState.value.planMorningId
                )
            }
        }
    }

    fun onEvent(event: ModalSheetNoteEvent) {
        val currentState = _state.value as? MorningNoteUiState.Success ?: return

        when (event) {
            is ModalSheetNoteEvent.ClearQuery -> {
                updateIfSuccess {
                    it.copy(
                        query = "",
                        visibleAddField = false,
                        visibleEditFieldId = null
                    )
                }
            }

            is ModalSheetNoteEvent.OpenAddNoteField -> {
                updateIfSuccess {
                    it.copy(
                        query = "",
                        visibleAddField = true,
                        visibleEditFieldId = null
                    )
                }
            }

            is ModalSheetNoteEvent.OnQueryChange -> updateIfSuccess { it.copy(query = event.s) }

            is ModalSheetNoteEvent.AddNote -> {
                viewModelScope.launch {
                    morningRepo.insertMorning(
                        DataMorning(
                            id = 0,
                            note = currentState.query,
                            date = LocalDate.now().toString(),
                            planId = currentState.currentPlanId
                        )
                    )

                    val notes = morningRepo.getNotes()

                    updateIfSuccess {
                        it.copy(
                            visibleAddField = false,
                            query = "",
                            items = notes
                        )
                    }
                }
            }

            is ModalSheetNoteEvent.EditNote -> {
                viewModelScope.launch {
                    val item =
                        currentState.items.first { it.id == currentState.visibleEditFieldId }
                    morningRepo.updateMorning(item.copy(note = currentState.query))
                    val notes = morningRepo.getNotes()

                    updateIfSuccess {
                        it.copy(
                            visibleAddField = false,
                            visibleEditFieldId = null,
                            query = "",
                            items = notes
                        )
                    }
                }
            }

            is ModalSheetNoteEvent.OpenEditNoteField -> {
                updateIfSuccess {
                    it.copy(
                        query = currentState.items.first { it.id == event.id }.note.toString(),
                        visibleAddField = false,
                        visibleEditFieldId = event.id
                    )
                }
            }

            is ModalSheetNoteEvent.ClearNote -> {
                viewModelScope.launch {
                    val item = currentState.items.first { it.id == event.id }
                    morningRepo.updateMorning(item.copy(note = ""))
                    val notes = morningRepo.getNotes()

                    updateIfSuccess {
                        it.copy(
                            visibleAddField = false,
                            visibleEditFieldId = null,
                            query = "",
                            items = notes
                        )
                    }
                }
            }
        }
    }

    private fun updateIfSuccess(transform: (MorningNoteUiState.Success) -> MorningNoteUiState.Success) {
        _state.update { currentState ->
            if (currentState is MorningNoteUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}