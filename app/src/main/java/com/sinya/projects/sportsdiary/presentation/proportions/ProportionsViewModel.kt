package com.sinya.projects.sportsdiary.presentation.proportions

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.repository.ProportionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProportionsViewModel @Inject constructor(
    private val repo: ProportionRepository
) : ViewModel() {

    private val _state = mutableStateOf<ProportionsUiState>(ProportionsUiState.Loading)
    val state: State<ProportionsUiState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = ProportionsUiState.Success(
                    proportions = repo.proportionList()
                )
            }
            catch (e: Exception) {
                _state.value = ProportionsUiState.Error(
                    message = "Ошибка загрузки данных: ${e.message}"
                )
            }
        }
    }

    fun onEvent(event: ProportionsEvent) {
        val currentState = _state.value as? ProportionsUiState.Success ?: return

        when(event) {
            ProportionsEvent.DeleteProportion -> {
                viewModelScope.launch {
                    val id = currentState.deleteDialogId
                    id?.let { repo.delete(currentState.proportions.first {id == it.id }) }
                    _state.value = ProportionsUiState.Success(
                        proportions = repo.proportionList(),
                        deleteDialogId = null
                    )
                }
            }
            is ProportionsEvent.OpenDialog -> {
                _state.value = currentState.copy(
                    deleteDialogId = event.id
                )
            }
        }
    }
}