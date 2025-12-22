package com.sinya.projects.sportsdiary.presentation.proportionPage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.repository.ProportionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProportionPageViewModel @Inject constructor(
    private val repo: ProportionRepository
) : ViewModel() {

    private val _state = mutableStateOf<ProportionPageUiState>(ProportionPageUiState.Loading)
    val state: State<ProportionPageUiState> = _state

    fun init(id: Int?) {
        viewModelScope.launch {
            val entity = repo.getById(id)
            _state.value = ProportionPageUiState.Success(item = entity)
        }
    }

    fun onEvent(event: ProportionPageUiEvent) {
        val currentState = _state.value as? ProportionPageUiState.Success ?: return

        when (event) {
            is ProportionPageUiEvent.Save -> {
                viewModelScope.launch {
                    repo.insertProportions(currentState.item)
                }
            }
            is ProportionPageUiEvent.OnChangeValue -> {
                val list = currentState.item.items.map {
                    if (event.id == it.id) it.copy(value = event.value)
                    else it
                }
                _state.value = currentState.copy(item = currentState.item.copy(items = list))
            }

            is ProportionPageUiEvent.OpenDialog -> {
                viewModelScope.launch {
                    if (event.id!=null) {
                        val proportion = repo.getProportionData(event.id)
                        _state.value = currentState.copy(
                            dialogContent = proportion
                        )
                    }
                    else {
                        _state.value = currentState.copy(
                            dialogContent = null
                        )
                    }
                }
            }
        }
    }
}