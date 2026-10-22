package com.sinya.projects.pageproportion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.error.SavedChanges
import com.sinya.projects.domain.error.SavingProcess
import com.sinya.projects.domain.useCase.GetMeasurementDataUseCase
import com.sinya.projects.domain.useCase.ObserveNewProportionEntityUseCase
import com.sinya.projects.domain.useCase.ObserveProportionEntityUseCase
import com.sinya.projects.domain.useCase.UpsertProportionUseCase
import com.sinya.projects.domain.utils.UndoManager
import com.sinya.projects.domain.utils.getErrorMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = ProportionPageViewModel.Factory::class)
internal class ProportionPageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,
    private val observeProportionEntityUseCase: ObserveProportionEntityUseCase,
    private val observeNewProportionEntityUseCase: ObserveNewProportionEntityUseCase,
    private val upsertProportionUseCase: UpsertProportionUseCase,
    private val getMeasurementDataUseCase: GetMeasurementDataUseCase
) : ViewModel() {

    private val undoManager = UndoManager<ProportionPageUiState.ProportionForm>()
    private var undoJob: Job? = null

    private val _state = MutableStateFlow<ProportionPageUiState>(ProportionPageUiState.Loading)
    val state: StateFlow<ProportionPageUiState> = _state.asStateFlow()

    private val measureId = MutableStateFlow(id)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): ProportionPageViewModel
    }

    init {
        observeProportionItem()
        observeSavingChanges()
    }

    private fun observeProportionItem() = viewModelScope.launch {
        measureId
            .flatMapLatest { id ->
                when (id) {
                    null -> observeNewProportionEntityUseCase()
                    else -> observeProportionEntityUseCase(id)
                }
            }
            .onEach { entity ->
                _state.update { currentState ->
                    when (currentState) {
                        is ProportionPageUiState.Error -> ProportionPageUiState.ProportionForm(item = entity)
                        ProportionPageUiState.Loading -> ProportionPageUiState.ProportionForm(item = entity)
                        is ProportionPageUiState.ProportionForm -> currentState.copy(item = entity)
                        else -> currentState
                    }
                }
            }
            .catch { error ->
                _state.update {
                    ProportionPageUiState.Error(
                        errorMessage = error.message ?: "Unknown error"
                    )
                }
            }
            .collect()
    }

    private fun observeSavingChanges() = viewModelScope.launch {
        _state
            .filterIsInstance<ProportionPageUiState.ProportionForm>()
            .filter { it.hasUnsavedChanges && !it.isSaving }
            .map { it.item }
            .distinctUntilChanged()
            .debounce(2000)
            .onEach { showMessage(SavingProcess()) }
            .collectLatest {
                updateIfForm { it.copy(isSaving = true) }
                saveMeasurements {
                    updateIfForm {
                        it.copy(
                            hasUnsavedChanges = false,
                            isSaving = false
                        )
                    }
                }
            }
    }

    fun onEvent(event: ProportionPageEvent) {
        when (event) {
            ProportionPageEvent.Save -> saveMeasurements {
                _state.update { ProportionPageUiState.Success }
            }

            is ProportionPageEvent.OpenDialog -> openDialog(event.id)

            ProportionPageEvent.OnErrorShown -> updateIfForm { it.copy(errorMessage = null) }

            is ProportionPageEvent.CalendarState -> updateIfForm { it.copy(calendarVisible = event.state) }

            is ProportionPageEvent.Undoable -> handleUndoable(event.action)

            is ProportionPageEvent.Undo -> undo()

            is ProportionPageEvent.Redo -> redo()
        }
    }

    private fun handleUndoable(action: ProportionUndoableAction) {
        undoManager.push(action)
        updateIfForm { action.apply(it).copy(
            canUndo = undoManager.canUndo,
            canRedo = undoManager.canRedo
        )  }
    }

    fun undo() {
        if (undoJob?.isActive == true) return
        undoJob = viewModelScope.launch {
            updateIfForm { it.copy(isUndoing = true) }
            delay(200)
            updateIfForm { form ->
                (undoManager.undo(form) ?: form).copy(
                    canUndo = undoManager.canUndo,
                    canRedo = undoManager.canRedo
                )
            }
            updateIfForm { it.copy(isUndoing = false) }
        }
    }

    fun redo() {
        if (undoJob?.isActive == true) return
        undoJob = viewModelScope.launch {
            updateIfForm { it.copy(isUndoing = true) }
            delay(200)
            updateIfForm { form ->
                (undoManager.redo(form) ?: form).copy(
                    canUndo = undoManager.canUndo,
                    canRedo = undoManager.canRedo
                )
            }
            updateIfForm { it.copy(isUndoing = false) }
        }
    }

    private fun saveMeasurements(function: () -> Unit) = viewModelScope.launch {
        val currentState = _state.value as? ProportionPageUiState.ProportionForm ?: return@launch

        upsertProportionUseCase(currentState.item).fold(
            onSuccess = { id ->
                updateIfForm { it.copy(errorMessage = SavedChanges().getErrorMessage()) }
                measureId.update { id }
                function()
            },
            onFailure = { error ->
                _state.value = ProportionPageUiState.Error(error.toString())
            }
        )
    }

    private fun showMessage(message: Throwable?) = updateIfForm {
        it.copy(errorMessage = message?.getErrorMessage())
    }

    private fun openDialog(id: Int?) {
        when (id) {
            null -> updateIfForm { it.copy(dialogContent = null) }
            else -> {
                viewModelScope.launch {
                    getMeasurementDataUseCase(id).fold(
                        onSuccess = { data ->
                            updateIfForm { it.copy(dialogContent = data) }
                        },
                        onFailure = { error ->
                            _state.value = ProportionPageUiState.Error(error.toString())
                        }
                    )
                }
            }
        }
    }

    private fun updateIfForm(transform: (ProportionPageUiState.ProportionForm) -> ProportionPageUiState.ProportionForm) {
        _state.update { currentState ->
            if (currentState is ProportionPageUiState.ProportionForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}