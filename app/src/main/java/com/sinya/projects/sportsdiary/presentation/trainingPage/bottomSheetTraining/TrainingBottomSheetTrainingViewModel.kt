package com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataTraining
import com.sinya.projects.sportsdiary.data.database.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory.ExerciseUi
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingBottomSheetTrainingViewModel @Inject constructor(
    private val repoEx: ExercisesRepository,
    private val repoTr: TrainingRepository
) : ViewModel() {

    private val _state = mutableStateOf<TrainingBottomSheetTrainingUiState>(TrainingBottomSheetTrainingUiState.Loading)
    val state: State<TrainingBottomSheetTrainingUiState> = _state

    fun init(id: Int) {
        viewModelScope.launch {
            _state.value = TrainingBottomSheetTrainingUiState.Success(
                id = id,
                items = repoEx.getExercisesList().map { ExerciseUi(it.id, it.name) }
            )
        }
    }

    fun onEvent(event: TrainingBottomSheetTrainingUiEvent) {
        val currentState = _state.value as? TrainingBottomSheetTrainingUiState.Success ?: return

        when(event) {
            is TrainingBottomSheetTrainingUiEvent.OnQueryChange -> {
                _state.value = currentState.copy(
                    query = event.s
                )
            }
            is TrainingBottomSheetTrainingUiEvent.ToggleExpanded -> {
                _state.value = currentState.copy(
                    expanded = !currentState.expanded
                )
            }
            is TrainingBottomSheetTrainingUiEvent.Toggle -> {
                _state.value = currentState.copy(
                    items = currentState.items.map { if (it.id == event.id) it.copy(checked = !it.checked) else it }
                )
            }
            is TrainingBottomSheetTrainingUiEvent.SetAll -> {
                _state.value = currentState.copy(
                    items = currentState.items.map { it.copy(checked = event.checked) }
                )
            }
            is TrainingBottomSheetTrainingUiEvent.AddTrainings -> {
                create(event.onDone, event.onError)
            }
        }
    }

    fun filtered() : List<ExerciseUi> {
        val currentState = _state.value as? TrainingBottomSheetTrainingUiState.Success ?: return listOf()
        return if (currentState.query.isBlank()) currentState.items else currentState.items.filter { it.name.contains(currentState.query, ignoreCase = true) }
    }
    private fun selectedIds(): List<Int> {
        val currentState = _state.value as? TrainingBottomSheetTrainingUiState.Success ?: return listOf()
        return currentState.items.filter { it.checked }.map { it.id }
    }

    fun triState(): ToggleableState {
        val currentState = _state.value as? TrainingBottomSheetTrainingUiState.Success ?: return ToggleableState.Off

        val total = currentState.items.size
        val sel = currentState.items.count { it.checked }
        return when (sel) {
            0 -> ToggleableState.Off
            total -> ToggleableState.On
            else -> ToggleableState.Indeterminate
        }
    }

    private fun create(onDone: () -> Unit, onError: (Throwable) -> Unit) {
        val currentState = _state.value as? TrainingBottomSheetTrainingUiState.Success ?: return

        val ids = selectedIds()
        if (ids.isEmpty()) {
            onError(IllegalStateException("Заполните название и выберите упражнения"))
            return
        }
        viewModelScope.launch {
            runCatching {
                repoTr.insertDataTraining(ids.map {
                DataTraining(
                    trainingId = currentState.id,
                    exerciseId = it,
                    countResult = "0/0/0/0",
                    weightResult = "0/0/0/0"
                )
            }) }
                .onSuccess { onDone() }
                .onFailure {
                    onDone() }
        }
    }
}
