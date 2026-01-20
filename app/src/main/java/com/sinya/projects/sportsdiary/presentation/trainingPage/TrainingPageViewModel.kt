package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.domain.model.addEmptySet
import com.sinya.projects.sportsdiary.domain.model.removeSet
import com.sinya.projects.sportsdiary.domain.model.updateSet
import com.sinya.projects.sportsdiary.domain.useCase.GetCategoriesListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExercisesByCategoryUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetSerialNumOfCategoryUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetTrainingItemUseCase
import com.sinya.projects.sportsdiary.domain.useCase.UpsertTrainingUseCase
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

@HiltViewModel(assistedFactory = TrainingPageViewModel.Factory::class)
class TrainingPageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,

    private val getCategoriesListUseCase: GetCategoriesListUseCase,
    private val upsertTrainingUseCase: UpsertTrainingUseCase,
    private val getTrainingItemUseCase: GetTrainingItemUseCase,
    private val getSerialNumOfCategoryUseCase: GetSerialNumOfCategoryUseCase,
    private val getExercisesByCategoryUseCase: GetExercisesByCategoryUseCase,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TrainingPageUiState>(TrainingPageUiState.Loading)
    val state: StateFlow<TrainingPageUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): TrainingPageViewModel
    }

    init {
        loadData(id)
    }

    fun onEvent(event: TrainingPageEvent) {
        when (event) {
            is TrainingPageEvent.OnSelectedCategory -> onSelectedCategory(event.category)

            is TrainingPageEvent.CalendarState -> updateIfForm { it.copy(calendarVisible = event.state) }

            is TrainingPageEvent.AddSet -> addSet(event.id)

            is TrainingPageEvent.DeleteSet -> removeSet(event.id, event.index)

            is TrainingPageEvent.EditSet -> {
                editSet(
                    exerciseId = event.exId,
                    index = event.index,
                    value = event.value,
                    valState = event.valState
                )
            }

            is TrainingPageEvent.OpenBottomSheetCategory -> saveTraining {
                updateIfForm { it.copy(bottomSheetCategoryStatus = event.state) }
            }

            is TrainingPageEvent.OpenBottomSheetTraining -> saveTraining {
                updateIfForm { it.copy(bottomSheetTrainingStatus = event.state) }
            }

            is TrainingPageEvent.OpenDialog -> openDialog(event.id)

            is TrainingPageEvent.Delete -> deleteExercise(event.id)

            is TrainingPageEvent.OnPickDate -> updateIfForm {
                it.copy(
                    item = it.item.copy(
                        date = parseMillisToDateTime(
                            event.millis
                        )
                    )
                )
            }

            is TrainingPageEvent.MoveExercise -> moveExercise(event.from, event.to)

            is TrainingPageEvent.UpdateListTraining -> getTrainingEntity(event.id)

            TrainingPageEvent.Save -> saveTraining {
                _state.value = TrainingPageUiState.Success
            }

            TrainingPageEvent.UpdateCategories -> getCategoriesList()

            TrainingPageEvent.OnErrorShown -> updateIfForm { it.copy(errorMessage = null) }
        }
    }

    private fun onSelectedCategory(category: TypeTraining) = viewModelScope.launch {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return@launch

        getExercisesByCategoryUseCase(category.id).fold(
            onSuccess = { items ->
                val title = getSerialNumOfCategoryUseCase(category.id).getOrElse { s.item.id.toString() }
                updateIfForm { trainingForm ->
                    trainingForm.copy(
                        item = trainingForm.item.copy(
                            items = items,
                            title = title,
                            category = category
                        )
                    )
                }
            },
            onFailure = { error ->
                _state.value = TrainingPageUiState.Error(errorMessage = error.toString())
            }
        )
    }

    private fun loadData(id: Int?) = viewModelScope.launch {
        getTrainingEntity(id)
        getCategoriesList()
    }

    private fun getCategoriesList() = viewModelScope.launch {
        getCategoriesListUseCase().fold(
            onSuccess = { list ->
                updateIfForm { it.copy(categories = list) }
            },
            onFailure = { error ->
                _state.value = TrainingPageUiState.Error(errorMessage = error.toString())
            }
        )
    }

    private fun getTrainingEntity(id: Int? = this.id) = viewModelScope.launch {
        getTrainingItemUseCase(id).fold(
            onSuccess = { item ->
                if (_state.value is TrainingPageUiState.TrainingForm) {
                    updateIfForm {
                        it.copy(
                            item = item,
                            bottomSheetCategoryStatus = false,
                            bottomSheetTrainingStatus = false,
                            calendarVisible = false,
                            dialogContent = null,
                            errorMessage = null
                        )
                    }
                } else {
                    _state.value = TrainingPageUiState.TrainingForm(
                        item = item
                    )
                }
            },
            onFailure = { error ->
                _state.value = TrainingPageUiState.Error(errorMessage = error.toString())
            }
        )
    }

    private fun openDialog(id: Int?) = viewModelScope.launch {
        if (id != null) {
            getExerciseDescriptionUseCase(id).fold(
                onSuccess = { exercise ->
                    updateIfForm {
                        it.copy(
                            dialogContent = ExerciseDialogContent(
                                id = exercise.exerciseId,
                                name = exercise.name,
                                description = exercise.description,
                            )
                        )
                    }
                },
                onFailure = { error ->
                    updateIfForm { it.copy(errorMessage = error.toString()) }
                }
            )
        } else {
            updateIfForm {
                it.copy(dialogContent = null)
            }
        }
    }

    private fun saveTraining(function: () -> Unit) = viewModelScope.launch {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return@launch

        upsertTrainingUseCase(s.item).fold(
            onSuccess = { id ->
                updateIfForm { it.copy(item = it.item.copy(id = id)) }
                function()
            },
            onFailure = { error -> _state.value = TrainingPageUiState.Error(error.toString()) }
        )
    }

    private fun deleteExercise(exerciseId: Int?) {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

        updateIfForm {
            it.copy(
                item = s.item.copy(
                    items = s.item.items.filterNot { item -> item.id == exerciseId }
                )
            )
        }
    }

    private fun moveExercise(from: Int, to: Int) {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

        updateIfForm {
            val mutable = s.item.items.toMutableList()

            if (from !in mutable.indices) return@updateIfForm it
            if (from == to) return@updateIfForm it

            val moved = mutable.removeAt(from)

            val target = to.coerceIn(0, mutable.size)
            mutable.add(target, moved)

            it.copy(
                item = s.item.copy(
                    items = mutable.mapIndexed { index, item ->
                        item.copy(orderIndex = index)
                    }
                )
            )
        }
    }

    private fun addSet(exerciseId: Int) {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

        _state.value = s.copy(
            item = s.item.copy(
                items = s.item.items.map { ex ->
                    if (ex.id != exerciseId) ex
                    else ex.addEmptySet()
                }
            )
        )
    }

    private fun editSet(exerciseId: Int, index: Int, value: String?, valState: Boolean) {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return
        val newItems = s.item.items.map { ex ->
            if (ex.id != exerciseId) ex
            else {
                if (index !in ex.item.indices) ex
                else {
                    if (valState) ex.updateSet(index, value, null)
                    else ex.updateSet(index, null, value)
                }
            }
        }

        _state.value = s.copy(item = s.item.copy(items = newItems))
    }

    private fun removeSet(exerciseId: Int, index: Int) {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

        _state.value = s.copy(
            item = s.item.copy(
                items = s.item.items.map { ex ->
                    if (ex.id != exerciseId) ex
                    else {
                        if (index !in ex.item.indices) ex
                        else ex.removeSet(index)
                    }
                }
            )
        )
    }

    private fun updateIfForm(transform: (TrainingPageUiState.TrainingForm) -> TrainingPageUiState.TrainingForm) {
        _state.update { currentState ->
            if (currentState is TrainingPageUiState.TrainingForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}