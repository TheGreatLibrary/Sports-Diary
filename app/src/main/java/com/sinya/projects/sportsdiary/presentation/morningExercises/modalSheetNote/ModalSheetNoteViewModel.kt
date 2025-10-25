package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

import android.icu.util.LocaleData
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class ModalSheetNoteViewModel @Inject constructor(
    private val morningRepo: MorningRepository
) : ViewModel() {

    private val _state = mutableStateOf<ModalSheetNoteUiState>(ModalSheetNoteUiState.Loading)
    val state: State<ModalSheetNoteUiState> = _state

    init {
        viewModelScope.launch {
            _state.value = ModalSheetNoteUiState.Success(
                items = morningRepo.getNotes()
            )
        }
    }

    fun onEvent(event: ModalSheetNoteEvent) {
        val currentState = _state.value as? ModalSheetNoteUiState.Success ?: return

        when(event) {
            is ModalSheetNoteEvent.UpdateCurrent -> {
                
            }

            is ModalSheetNoteEvent.OpenAddNoteField -> {
                    _state.value = currentState.copy(
                        visibleAddField = true
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
        }
    }



}