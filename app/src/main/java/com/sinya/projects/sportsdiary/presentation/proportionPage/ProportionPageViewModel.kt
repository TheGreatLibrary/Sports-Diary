package com.sinya.projects.sportsdiary.presentation.proportionPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.useCase.GetMeasurementDataUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetProportionItemUseCase
import com.sinya.projects.sportsdiary.domain.useCase.InsertOrUpdateProportionUseCase
import com.sinya.projects.sportsdiary.utils.parseMillisToDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ProportionPageViewModel.Factory::class)
class ProportionPageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,

    private val getProportionItemUseCase: GetProportionItemUseCase,
    private val insertOrUpdateProportionUseCase: InsertOrUpdateProportionUseCase,
    private val getMeasurementDataUseCase: GetMeasurementDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProportionPageUiState>(ProportionPageUiState.Loading)
    val state: StateFlow<ProportionPageUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): ProportionPageViewModel
    }

    init {
        loadData(id)
    }

    fun onEvent(event: ProportionPageEvent) {
        when (event) {
            ProportionPageEvent.Save -> saveMeasurements()

            is ProportionPageEvent.OnChangeValue -> onChangeValue(event.id, event.value)

            is ProportionPageEvent.OpenDialog -> openDialog(event.id)

            ProportionPageEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            is ProportionPageEvent.CalendarState -> updateIfSuccess { it.copy(calendarVisible = event.state) }

            is ProportionPageEvent.OnPickDate -> onPickDate(event.millis)
        }
    }

    private fun loadData(id: Int?) = viewModelScope.launch {
        getProportionItemUseCase(id).fold(
            onSuccess = { item ->
                _state.value = ProportionPageUiState.Success(item = item)
            },
            onFailure = { error ->
                _state.value = ProportionPageUiState.Error(error.toString())
            }
        )
    }

    private fun saveMeasurements() = viewModelScope.launch {
        val currentState = _state.value as? ProportionPageUiState.Success ?: return@launch

        insertOrUpdateProportionUseCase(currentState.item).onFailure { error ->
            _state.value = ProportionPageUiState.Error(error.toString())
        }
    }

    private fun onPickDate(millis: Long?) = viewModelScope.launch {
        val currentState = _state.value as? ProportionPageUiState.Success ?: return@launch

        val item = currentState.item.copy(date = parseMillisToDateTime(millis))

        updateIfSuccess { it.copy(item = item) }
    }

    private fun openDialog(id: Int?) = viewModelScope.launch {
        if (id!=null) {
            getMeasurementDataUseCase(id).fold(
                onSuccess = { data ->
                    updateIfSuccess { it.copy( dialogContent = data) }
                },
                onFailure = { error ->
                    _state.value = ProportionPageUiState.Error(error.toString())
                }
            )
        }
        else {
           updateIfSuccess {
               it.copy(dialogContent = null)
           }
        }
    }

    private fun onChangeValue(id: Int?, value: String) {
        val currentState = _state.value as? ProportionPageUiState.Success ?: return

        val list = currentState.item.items.map {
            if (id == it.id) it.copy(value = value)
            else it
        }
        updateIfSuccess {
            it.copy(item = currentState.item.copy(items = list))
        }
    }

    private fun updateIfSuccess(transform: (ProportionPageUiState.Success) -> ProportionPageUiState.Success) {
        _state.update { currentState ->
            if (currentState is ProportionPageUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}