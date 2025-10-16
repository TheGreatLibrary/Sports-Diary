package com.sinya.projects.sportsdiary.presentation.trainingPage

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import com.sinya.projects.sportsdiary.presentation.trainings.Training
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch


@HiltViewModel
class TrainingPageViewModel @Inject constructor(
    private val repo: TrainingRepository
) : ViewModel() {

    private val _state = mutableStateOf<TrainingPageUiState>(TrainingPageUiState.Loading)
    val state: State<TrainingPageUiState> = _state

    fun init(id: Int?) {
        viewModelScope.launch {
            val entity = repo.getById(id)
            Log.d("", "${entity}")
            val categories = repo.categoriesList()
            _state.value = TrainingPageUiState.Success(
                id = entity.id,
                title = entity.title,
                category = entity.category,
                date = entity.date,
                items = entity.items,
                categories = categories
            )
        }
    }

    fun onEvent(event: TrainingPageUiEvent) {
        val s = _state.value as? TrainingPageUiState.Success ?: return
        when (event) {
            is TrainingPageUiEvent.OnSelectedCategory -> {
                viewModelScope.launch {
                    _state.value = s.copy(
                        category = event.name,
                        title = repo.getSerialNumOfCategory(event.name.id)
                    )
                }
            }

            is  TrainingPageUiEvent.UpdateListTraining -> {
                viewModelScope.launch {
                    val entity = repo.getById(s.id)
                    _state.value = s.copy(
                        items = entity.items
                    )
                }
            }

            is TrainingPageUiEvent.AddSet -> {
                addSet(event.id)
            }

            is TrainingPageUiEvent.DeleteSet -> {
                removeSet(event.id, event.index)
            }

            is TrainingPageUiEvent.EditSet -> {
                editSet(event.exId, event.index, event.value, event.valState)
            }

            is TrainingPageUiEvent.OpenBottomSheetCategory -> {
                _state.value = s.copy(bottomSheetCategoryStatus = event.state)
            }

            is TrainingPageUiEvent.OpenBottomSheetTraining -> {
                viewModelScope.launch {
                    save()
                }
                _state.value = s.copy(bottomSheetTrainingStatus = event.state)
            }

            is TrainingPageUiEvent.AddExercise -> {
                val newId = (s.items.maxOfOrNull { it.id } ?: 0) + 1
                val newItems = s.items + ExerciseItem(
                    id = newId,
                    title = event.title,
                    countList = listOf(""),
                    weightList = listOf("")
                )
                _state.value = s.copy(items = newItems)
            }

            is TrainingPageUiEvent.Delete -> {
                _state.value = s.copy(items = s.items.filterNot { it.id == event.id })
            }

            is TrainingPageUiEvent.Save -> {
                viewModelScope.launch {
                    save()
                }
                event.exit()
            }
            is TrainingPageUiEvent.UpdateCategories -> {
                updateCategoriesList()
            }
        }
    }

    private suspend fun save() {
        val s = _state.value as? TrainingPageUiState.Success ?: return

        val item = TrainingEntity(
            id = s.id,
            title = s.title,
            category = s.category,
            date = s.date,
            items = s.items
        )
        repo.insertTraining(item)
    }

    private fun addSet(exId: Int) {
        val s = _state.value as? TrainingPageUiState.Success ?: return
        val newItems = s.items.map { ex ->
            if (ex.id != exId) ex else {
                val newCounts = ex.countList.toMutableList().apply { add("") }
                val newWeights = ex.weightList.toMutableList().apply { add("") }
                ex.copy(countList = newCounts, weightList = newWeights)
            }
        }
        _state.value = s.copy(items = newItems)
    }

    private fun removeSet(exId: Int, index: Int) {
        val s = _state.value as? TrainingPageUiState.Success ?: return
        val newItems = s.items.map { ex ->
            if (ex.id != exId) ex else {
                if (index !in ex.countList.indices) ex else {
                    val newCounts = ex.countList.toMutableList().also { it.removeAt(index) }
                    val newWeights = ex.weightList.toMutableList().also { it.removeAt(index) }
                    ex.copy(countList = newCounts, weightList = newWeights)
                }
            }
        }
        _state.value = s.copy(items = newItems)
    }

    private fun editSet(exId: Int, index: Int, value: String?, valState: Boolean) {
        val s = _state.value as? TrainingPageUiState.Success ?: return
        val newItems = s.items.map { ex ->
            if (ex.id != exId) ex else {
                if (index !in ex.countList.indices) ex else {
                    if (valState) {
                        val newCounts = ex.countList.toMutableList().apply {
                            if (value != null) this[index] = value
                        }
                        ex.copy(countList = newCounts)
                    } else {
                        val newWeights = ex.weightList.toMutableList().apply {
                            if (value != null) this[index] = value
                        }
                        ex.copy(weightList = newWeights)
                    }


                }
            }
        }
        _state.value = s.copy(items = newItems)
    }

    private fun updateCategoriesList() {
        val s = _state.value as? TrainingPageUiState.Success ?: return

        viewModelScope.launch {
            _state.value = s.copy(
                categories = repo.categoriesList()
            )
        }
    }
}