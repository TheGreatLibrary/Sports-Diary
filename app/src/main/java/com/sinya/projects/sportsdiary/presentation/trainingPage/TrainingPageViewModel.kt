package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataTraining
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.model.BottomSheetCategoryData
import com.sinya.projects.sportsdiary.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.domain.model.ExerciseUi
import com.sinya.projects.sportsdiary.domain.model.addEmptySet
import com.sinya.projects.sportsdiary.domain.model.removeSet
import com.sinya.projects.sportsdiary.domain.model.updateSet
import com.sinya.projects.sportsdiary.domain.useCase.CheckNameCategoryExistsUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetCategoriesListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExercisesByCategoryUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetSerialNumOfCategoryUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetTrainingItemUseCase
import com.sinya.projects.sportsdiary.domain.useCase.InsertCategoryWithDataUseCase
import com.sinya.projects.sportsdiary.domain.useCase.InsertDataTrainingUseCase
import com.sinya.projects.sportsdiary.domain.useCase.UpsertTrainingUseCase
import com.sinya.projects.sportsdiary.utils.parseMillisToDateTime
import com.sinya.projects.sportsdiary.utils.searchByTerms
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = TrainingPageViewModel.Factory::class)
class TrainingPageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,

    private val getCategoriesListUseCase: GetCategoriesListUseCase,
    private val upsertTrainingUseCase: UpsertTrainingUseCase,
    private val getTrainingItemUseCase: GetTrainingItemUseCase,
    private val getSerialNumOfCategoryUseCase: GetSerialNumOfCategoryUseCase,
    private val getExercisesByCategoryUseCase: GetExercisesByCategoryUseCase,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,

    private val insertCategoryUseCase: InsertCategoryWithDataUseCase,
    private val checkNameCategoryExistsUseCase: CheckNameCategoryExistsUseCase,
    private val insertDataTrainingUseCase: InsertDataTrainingUseCase,
    private val getExerciseListUseCase: GetExerciseListUseCase
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

    fun filtered(): List<ExerciseUi> {
        val currentState = _state.value as? TrainingPageUiState.TrainingForm ?: return listOf()

        if (currentState.bottomSheetCategoryStatus != null) return currentState.items.searchByTerms(
            currentState.bottomSheetCategoryStatus.query
        ) { it.name }
        else if (currentState.bottomSheetTrainingQuery != null) return currentState.items.searchByTerms(
            currentState.bottomSheetTrainingQuery
        ) { it.name }
        return emptyList()
    }

    fun onEvent(event: TrainingPageEvent) {
        when (event) {
            is TrainingPageEvent.OnSelectedCategory -> onSelectedCategory(event.category)

            is TrainingPageEvent.CalendarState -> updateIfForm { it.copy(calendarVisible = event.state) }

            is TrainingPageEvent.OnPickDate -> updateIfForm {
                it.copy(
                    item = it.item.copy(
                        date = parseMillisToDateTime(
                            event.millis
                        )
                    )
                )
            }

            TrainingPageEvent.OpenBottomSheetCategory -> updateIfForm { form ->
                form.copy(
                    bottomSheetCategoryStatus = BottomSheetCategoryData(),
                    bottomSheetTrainingQuery = null,
                    items = form.items.map { it.copy(checked = false) }
                )
            }

            TrainingPageEvent.OpenBottomSheetTraining -> updateIfForm { form ->
                form.copy(
                    bottomSheetTrainingQuery = "",
                    bottomSheetCategoryStatus = null,
                    items = form.items.map { it.copy(checked = false) }
                )
            }

            is TrainingPageEvent.OpenDialog -> openDialog(event.id)

            is TrainingPageEvent.OpenDialogGuide -> openDialogGuide(event.title, event.descr)

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

            is TrainingPageEvent.Delete -> deleteExercise(event.id)

            is TrainingPageEvent.Toggle -> {
                updateIfForm { categoryForm ->
                    categoryForm.copy(
                        items = categoryForm.items.map {
                            if (it.id == event.id) it.copy(checked = !it.checked) else it
                        }
                    )
                }
            }

            is TrainingPageEvent.MoveExercise -> moveExercise(event.from, event.to)

            is TrainingPageEvent.UpdateListTraining -> getTrainingEntity(event.id)

            TrainingPageEvent.Save -> saveTraining {
                _state.value = TrainingPageUiState.Success
            }

            TrainingPageEvent.UpdateCategories -> getCategoriesList()

            TrainingPageEvent.OnErrorShown -> updateIfForm { it.copy(errorMessage = null) }

            TrainingPageEvent.AddExercise -> saveTraining {
                addExercises()
            }

            is TrainingPageEvent.OnCreateCategory -> saveTraining {
                createCategory(event.onDone)
            }

            is TrainingPageEvent.OnNameChange -> {
                val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

                if (s.bottomSheetCategoryStatus != null) updateIfForm {
                    it.copy(
                        bottomSheetCategoryStatus = it.bottomSheetCategoryStatus!!.copy(
                            categoryName = event.s
                        )
                    )
                }
            }

            is TrainingPageEvent.OnQueryChange -> {
                val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

                if (s.bottomSheetCategoryStatus != null) updateIfForm {
                    it.copy(
                        bottomSheetCategoryStatus = it.bottomSheetCategoryStatus!!.copy(
                            query = event.s
                        )
                    )
                }
                else updateIfForm {
                    it.copy(bottomSheetTrainingQuery = event.s)
                }
            }
        }
    }

    private fun onSelectedCategory(category: TypeTraining?) = viewModelScope.launch {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return@launch

        if (category != null) {
            getExercisesByCategoryUseCase(category.id).fold(
                onSuccess = { items ->
                    val title =
                        getSerialNumOfCategoryUseCase(category.id).getOrElse { s.item.id.toString() }
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
        } else {
            val title = getSerialNumOfCategoryUseCase(null).getOrElse { s.item.id.toString() }
            updateIfForm { trainingForm ->
                trainingForm.copy(
                    item = trainingForm.item.copy(
                        items = listOf(),
                        title = title,
                        category = null
                    )
                )
            }
        }
    }

    private fun loadData(id: Int?) = viewModelScope.launch {
        withContext(Dispatchers.IO) { getTrainingEntity(id) }
        withContext(Dispatchers.IO) { getExerciseList() }
        withContext(Dispatchers.IO) { getCategoriesList() }
    }


    private fun selectedIds(): List<Int> {
        val currentState = _state.value as? TrainingPageUiState.TrainingForm ?: return listOf()
        return currentState.items.filter { it.checked }.map { it.id }
    }

    private fun addExercises() = viewModelScope.launch {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return@launch

        if (s.item.id == null) return@launch
        val items = selectedIds().map { item ->
            DataTraining(
                trainingId = s.item.id,
                exerciseId = item,
                countResult = "0/0/0/0",
                weightResult = "0/0/0/0"
            )
        }

        insertDataTrainingUseCase(items).fold(
            onSuccess = {
                updateIfForm { form ->
                    form.copy(
                        bottomSheetTrainingQuery = "",
                        items = form.items.map { it.copy(checked = false) }
                    )
                }
                getTrainingEntity(s.item.id)
            },
            onFailure = { error -> updateIfForm { it.copy(errorMessage = error.toString()) } }
        )
    }

    private fun createCategory(onDone: () -> Unit) = viewModelScope.launch {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return@launch

        if (s.bottomSheetCategoryStatus != null) {
            if (s.bottomSheetCategoryStatus.categoryName.isEmpty()) updateIfForm {
                it.copy(
                    bottomSheetCategoryStatus = it.bottomSheetCategoryStatus!!.copy(
                        isError = true
                    )
                )
            }
            else if (checkNameCategoryExistsUseCase(
                    s.bottomSheetCategoryStatus.categoryName,
                    0
                ).getOrElse { true }
            ) {
                updateIfForm {
                    it.copy(
                        bottomSheetCategoryStatus = it.bottomSheetCategoryStatus!!.copy(
                            isError = true
                        )
                    )
                }
            } else {
                val items = selectedIds().mapIndexed { index, item ->
                    DataTypeTrainings(
                        typeId = 0,
                        exerciseId = item,
                        orderIndex = index
                    )
                }

                insertCategoryUseCase(
                    TypeTraining(0, s.bottomSheetCategoryStatus.categoryName),
                    items
                ).fold(
                    onSuccess = {
                        updateIfForm { form ->
                            form.copy(
                                bottomSheetTrainingQuery = null,
                                items = form.items.map { it.copy(checked = false) },
                                bottomSheetCategoryStatus = null
                            )
                        }
                        onDone()
                    },
                    onFailure = { error ->
                        updateIfForm {
                            it.copy(
                                bottomSheetCategoryStatus = it.bottomSheetCategoryStatus!!.copy(
                                    isError = true
                                ),
                                errorMessage = error.toString()
                            )
                        }
                    }
                )
            }
        }
    }

    private fun getCategoriesList() = viewModelScope.launch {
        getCategoriesListUseCase().fold(
            onSuccess = { list ->
                updateIfForm { it.copy(categories = listOf(null) + list) }
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
                            calendarVisible = false,
                            bottomSheetCategoryStatus = null,
                            bottomSheetTrainingQuery = "",
                            dialogContent = null,
                            errorMessage = null
                        )
                    }
                } else {
                    _state.value = TrainingPageUiState.TrainingForm(
                        item = item,
                    )
                }
            },
            onFailure = { error ->
                _state.value = TrainingPageUiState.Error(errorMessage = error.toString())
            }
        )
    }

    private fun getExerciseList() = viewModelScope.launch {
        val items = getExerciseListUseCase().getOrElse { emptyList() }

        updateIfForm { it.copy(items = items) }
    }

    private fun openDialogGuide(title: String, description: String) {
        updateIfForm {
            it.copy(
                dialogContent = ExerciseDialogContent(
                    id = -1,
                    name = title,
                    description = description,
                )
            )
        }
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
            onFailure = { error ->
                _state.value = TrainingPageUiState.Error(error.toString())
            }
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