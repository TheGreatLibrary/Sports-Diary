package com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.utils.searchByTerms
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingCategoryViewModel @Inject constructor(
    private val repoEx: ExercisesRepository,
    private val repoTr: TrainingRepository
) : ViewModel() {

    private val _state = mutableStateOf<TrainingCategoryUiState>(TrainingCategoryUiState.Loading)
    val state: State<TrainingCategoryUiState> = _state

    init {
        viewModelScope.launch {
            _state.value = TrainingCategoryUiState.Success(
                items = repoEx.getExercisesList().map { ExerciseUi(it.id, it.name) }
            )
        }
    }

    fun onEvent(event: TrainingCategoryEvent) {
        val currentState = _state.value as? TrainingCategoryUiState.Success ?: return

        when(event) {
            is TrainingCategoryEvent.OnNameChange -> {
                _state.value = currentState.copy(
                    categoryName = event.s
                )
            }
            is TrainingCategoryEvent.OnQueryChange -> {
                _state.value = currentState.copy(
                    query = event.s
                )
            }
            is TrainingCategoryEvent.ToggleExpanded -> {
                _state.value = currentState.copy(
                    expanded = !currentState.expanded
                )
            }
            is TrainingCategoryEvent.Toggle -> {
                _state.value = currentState.copy(
                    items = currentState.items.map { if (it.id == event.id) it.copy(checked = !it.checked) else it }
                )
            }
            is TrainingCategoryEvent.SetAll -> {
                _state.value = currentState.copy(
                    items = currentState.items.map { it.copy(checked = event.checked) }
                )
            }
            is TrainingCategoryEvent.CreateCategory -> {
                create(event.onDone, event.onError)
            }
        }
    }

    fun filtered() : List<ExerciseUi> {
        val currentState = _state.value as? TrainingCategoryUiState.Success ?: return listOf()
//        return if (currentState.query.isBlank()) currentState.items else currentState.items.filter { it.name.contains(currentState.query, ignoreCase = true) }
        return currentState.items.searchByTerms(currentState.query) { it.name }
    }

    private fun selectedIds(): List<DataTypeTrainings> {
        val currentState = _state.value as? TrainingCategoryUiState.Success ?: return listOf()
        return currentState.items.filter { it.checked }.mapIndexed { index, it ->
            DataTypeTrainings(
                typeId = 0,
                exerciseId = it.id,
                orderIndex = index
            )
        }
    }

    fun triState(): ToggleableState {
        val currentState = _state.value as? TrainingCategoryUiState.Success ?: return ToggleableState.Off

        val total = currentState.items.size
        val sel = currentState.items.count { it.checked }
        return when (sel) {
            0 -> ToggleableState.Off
            total -> ToggleableState.On
            else -> ToggleableState.Indeterminate
        }
    }

    private fun create(onDone: () -> Unit, onError: (Throwable) -> Unit) {
        val currentState = _state.value as? TrainingCategoryUiState.Success ?: return

        val nameLocal = currentState.categoryName.trim()
        val ids = selectedIds()
        Log.d("ggbg", ids.toString())
        if (nameLocal.isEmpty()) {
            onError(IllegalStateException("Заполните название и выберите упражнения"))
            return
        }
        viewModelScope.launch {
            runCatching { repoTr.insertCategory(nameLocal, ids) }
                .onSuccess { onDone() }
                .onFailure { onError(it) }
        }
    }
}
