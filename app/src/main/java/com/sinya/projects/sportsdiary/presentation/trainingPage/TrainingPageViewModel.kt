package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingPageViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val exerciseRepository: ExercisesRepository
) : ViewModel() {

    private val _state = mutableStateOf<TrainingPageUiState>(TrainingPageUiState.Loading)
    val state: State<TrainingPageUiState> = _state

    fun init(id: Int?) {
        viewModelScope.launch {
            val entity = trainingRepository.getById(id)
            val categories = trainingRepository.categoriesList()
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

    fun onEvent(event: TrainingPageEvent) {
        val s = _state.value as? TrainingPageUiState.Success ?: return
        when (event) {
            is TrainingPageEvent.OnSelectedCategory -> {
                viewModelScope.launch {
                    val items = trainingRepository.getDataByTypeTraining(event.name.id)
                    _state.value = s.copy(
                        category = event.name,
                        title = trainingRepository.getSerialNumOfCategory(event.name.id),
                        items = items

                    )
                }
            }

            is TrainingPageEvent.UpdateListTraining -> {
                viewModelScope.launch {
                    val entity = trainingRepository.getById(s.id)
                    _state.value = s.copy(
                        items = entity.items,
                        bottomSheetTrainingStatus = false
                    )
                }
            }

            is TrainingPageEvent.AddSet -> {
                addSet(event.id)
            }

            is TrainingPageEvent.DeleteSet -> {
                removeSet(event.id, event.index)
            }

            is TrainingPageEvent.EditSet -> {
                editSet(event.exId, event.index, event.value, event.valState)
            }

            is TrainingPageEvent.OpenBottomSheetCategory -> {
                _state.value = s.copy(bottomSheetCategoryStatus = event.state)
            }

            is TrainingPageEvent.OpenBottomSheetTraining -> {
                save()
                _state.value = s.copy(bottomSheetTrainingStatus = event.state)
            }

            is TrainingPageEvent.AddExercise -> {
                val newId = (s.items.maxOfOrNull { it.id } ?: 0) + 1
                val newItems = s.items + ExerciseItem(
                    id = newId,
                    title = event.title,
                    countList = listOf(""),
                    weightList = listOf("")
                )
                _state.value = s.copy(items = newItems)
                save()

            }

            is TrainingPageEvent.Delete -> {
                _state.value = s.copy(items = s.items.filterNot { it.id == event.id })
            }

            is TrainingPageEvent.Save -> {
                    save()
                event.exit()
            }

            is TrainingPageEvent.UpdateCategories -> {
                updateCategoriesList()
            }

            is TrainingPageEvent.OpenDialog -> {
                viewModelScope.launch {
                    if (event.id != null) {
                        val item = s.items.first { it.id == event.id }
                        val exercise =
                            exerciseRepository.getExerciseById(event.id, Locale.current.language)
                        _state.value = s.copy(
                            dialogContent = ExerciseDialogContent(
                                id = item.id,
                                name = exercise.name,
                                description = exercise.description,
                            )
                        )
                    } else {
                        _state.value = s.copy(
                            dialogContent = null
                        )
                    }
                }
            }
        }
    }

    private fun save() = viewModelScope.launch {
        val s = _state.value as? TrainingPageUiState.Success ?: return@launch

        val item = TrainingEntity(
            id = s.id,
            title = s.title,
            category = s.category,
            date = s.date,
            items = s.items
        )
        trainingRepository.insertTraining(item)
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
                categories = trainingRepository.categoriesList()
            )
        }
    }
}