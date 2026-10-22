package com.sinya.projects.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.useCase.DeleteNotesUseCase
import com.sinya.projects.domain.useCase.EditNoteByMorningUseCase
import com.sinya.projects.domain.useCase.GetNotesUseCase
import com.sinya.projects.ui.components.sort.ModeOfSorting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val editNoteByMorningUseCase: EditNoteByMorningUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: NotesEvent) {
        when(event) {
            is NotesEvent.SortParamChange -> {
                updateIfSuccess {
                    it.copy(
                        selectedMode = it.selectedMode.apply(event.onSelect) as ModeOfSorting.Time
                    )
                }
            }

            NotesEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            NotesEvent.ReloadData -> loadData()

            NotesEvent.DeleteNotes -> delete()

            NotesEvent.ClearedListToDelete -> updateIfSuccess {
                it.copy(
                    deleteItems = listOf(),
                    notes = it.notes.map { it.copy(selected = false) })
            }

            is NotesEvent.SelectNote -> updateIfSuccess {
                val newList = it.deleteItems.toMutableSet()

                val selected = !it.notes.first { it.id == event.id }.selected

                if (selected) newList.add(event.id) else newList.remove(event.id)
                it.copy(
                    deleteItems = newList.toList(),
                    notes = it.notes.map {
                        if (it.id == event.id) it.copy(selected = selected) else it
                    }
                )
            }

            is NotesEvent.SelectEditMode -> {
                val currentState = _state.value as? NotesUiState.Success ?: return

                updateIfSuccess {
                    currentState.copy(
                        notes = it.notes.map {
                            if (it.id == event.id) it.copy(edit = !it.edit) else it.copy(edit = false)
                        }
                    )
                }
            }

            is NotesEvent.OnValueChange -> {
                updateIfSuccess {
                    it.copy(
                        notes = it.notes.map {
                            if (event.id == it.id) it.copy(note = event.value) else it
                        }
                    )
                }
            }
        }
    }
    private fun delete() {
        val currentState = _state.value as? NotesUiState.Success ?: return

        if (currentState.deleteItems.isNotEmpty()) {
            viewModelScope.launch {
                deleteNotesUseCase(currentState.deleteItems).fold(
                    onSuccess = {
                        updateIfSuccess { it.copy(deleteItems = listOf()) }
                    },
                    onFailure = { error ->
                        updateIfSuccess { it.copy(errorMessage = error.toString()) }
                    }
                )
            }
        }
    }

    private fun loadData() {
        updateIfSuccess { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            getNotesUseCase()
//                .filterIsInstance<TrainingFormUiState.TrainingForm>()
//                .filter { it.hasUnsavedChanges && !it.isSaving }
                .collectLatest { notes ->
                if (_state.value is NotesUiState.Success) {
                    updateIfSuccess {
                        it.copy(
                            notes = notes,
                            isRefreshing = false
                        )
                    }
                } else {
                    _state.update {
                        NotesUiState.Success(notes = notes, isRefreshing = false)
                    }
                }
            }
        }
    }

    private fun updateIfSuccess(transform: (NotesUiState.Success) -> NotesUiState.Success) {
        _state.update { currentState ->
            if (currentState is NotesUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}