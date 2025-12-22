package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.domain.repository.MorningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class MorningNoteViewModel @Inject constructor(
    private val morningRepo: MorningRepository
) : ViewModel() {

    private val _state = mutableStateOf<MorningNoteUiState>(MorningNoteUiState.Loading)
    val state: State<MorningNoteUiState> = _state

    init {
        viewModelScope.launch {
            _state.value = MorningNoteUiState.Success(
                items = morningRepo.getNotes()
            )
        }
    }

    fun onEvent(event: ModalSheetNoteEvent) {
        val currentState = _state.value as? MorningNoteUiState.Success ?: return

        when(event) {
            is ModalSheetNoteEvent.ClearQuery -> {
                _state.value = currentState.copy(
                    query = "",
                    visibleAddField = false,
                    visibleEditFieldId = null
                )
            }

            is ModalSheetNoteEvent.OpenAddNoteField -> {
                _state.value = currentState.copy(
                    query = "",
                    visibleAddField = true,
                    visibleEditFieldId = null
                )
            }

            is ModalSheetNoteEvent.OnQueryChange -> {
                _state.value = currentState.copy(
                    query = event.s
                )
            }

            is ModalSheetNoteEvent.AddNote -> {
                viewModelScope.launch {
                    morningRepo.insertMorning(
                        DataMorning(
                            id = 0,
                            note = currentState.query,
                            date = LocalDate.now().toString(),
                            planId = 0
                        )
                    )
                    _state.value = currentState.copy(
                        visibleAddField = false,
                        query = "",
                        items = morningRepo.getNotes()
                    )
                }
            }

            is ModalSheetNoteEvent.EditNote -> {
                viewModelScope.launch {
                    val item = currentState.items.first() { it.id == currentState.visibleEditFieldId }
                    morningRepo.updateMorning(
                        item.copy(note = currentState.query)
                    )
                    _state.value = currentState.copy(
                        visibleAddField = false,
                        visibleEditFieldId = null,
                        query = "",
                        items = morningRepo.getNotes()
                    )
                }
            }

            is ModalSheetNoteEvent.OpenEditNoteField -> {
                _state.value = currentState.copy(
                    query = currentState.items.first { it.id == event.id }.note.toString(),
                    visibleAddField = false,
                    visibleEditFieldId = event.id
                )
            }

            is ModalSheetNoteEvent.ClearNote -> {
                viewModelScope.launch {
                    val item = currentState.items.first() { it.id == event.id }
                    morningRepo.updateMorning(
                        item.copy(note = "")
                    )
                    _state.value = currentState.copy(
                        visibleAddField = false,
                        visibleEditFieldId = null,
                        query = "",
                        items = morningRepo.getNotes()
                    )
                }

            }
        }
    }
}