package com.sinya.projects.sportsdiary.presentation.proportions

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.repository.ProportionRepository
import com.sinya.projects.sportsdiary.presentation.trainings.TrainingUiState
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

    fun onEvent(event: ProportionsUiEvent) {

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

}