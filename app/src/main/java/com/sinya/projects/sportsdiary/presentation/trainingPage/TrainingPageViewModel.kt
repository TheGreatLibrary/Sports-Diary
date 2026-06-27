package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.error.EmptyTitleError
import com.sinya.projects.sportsdiary.core.domain.error.SavedChanges
import com.sinya.projects.sportsdiary.core.domain.error.SavingProcess
import com.sinya.projects.sportsdiary.core.domain.error.TitleExistedError
import com.sinya.projects.sportsdiary.core.domain.model.BottomSheetCategoryData
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.model.SortParam
import com.sinya.projects.sportsdiary.core.domain.useCase.CheckNameCategoryExistsUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExerciseWithSortedDataUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExercisesByCategoryUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetNewTrainingUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetSerialNumOfCategoryUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.InsertCategoryWithDataUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.ObserveCategoriesListUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.ObserveTrainingEntityByIdUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.UpsertTrainingUseCase
import com.sinya.projects.sportsdiary.core.utils.UndoManager
import com.sinya.projects.sportsdiary.core.utils.getErrorMessage
import com.sinya.projects.sportsdiary.core.utils.searchByTerms
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = TrainingPageViewModel.Factory::class)
class TrainingPageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,
    @Assisted("date") private val date: String?,

    private val getExerciseListUseCase: GetExerciseWithSortedDataUseCase,
    private val getNewTrainingUseCase: GetNewTrainingUseCase,

    private val observeCategoriesListUseCase: ObserveCategoriesListUseCase,
    private val getTrainingItemUseCase: ObserveTrainingEntityByIdUseCase,

    private val getSerialNumOfCategoryUseCase: GetSerialNumOfCategoryUseCase,
    private val getExercisesByCategoryUseCase: GetExercisesByCategoryUseCase,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,
    private val checkNameCategoryExistsUseCase: CheckNameCategoryExistsUseCase,

    private val upsertTrainingUseCase: UpsertTrainingUseCase,
    private val insertCategoryUseCase: InsertCategoryWithDataUseCase,
) : ViewModel() {

    private val undoManager = UndoManager<TrainingPageUiState.TrainingForm>()
    private var undoJob: Job? = null
    private val _state = MutableStateFlow<TrainingPageUiState>(TrainingPageUiState.Loading)
    val state: StateFlow<TrainingPageUiState> = _state.asStateFlow()

    private val trainingId = MutableStateFlow(id)

    @OptIn(FlowPreview::class)
    val filteredList: StateFlow<List<ExerciseWithMuscles>> = state
        .map { form ->
            when (form) {
                is TrainingPageUiState.TrainingForm -> {
                    val modeFiltered = form.modes.fold(form.items) { exercises, mode ->
                        mode.filter(exercises)
                    }

                    val query = form.bottomSheetCategoryStatus?.query
                        ?: form.bottomSheetTrainingQuery

                    if (query != null) {
                        modeFiltered.searchByTerms(query, { it.name })
                    } else {
                        modeFiltered
                    }
                }

                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?,
            @Assisted("date") date: String?
        ): TrainingPageViewModel
    }

    init {
        observeTrainingEntity()
        observeCategoriesList()
        observeSavingChanges()
    }

    private fun observeCategoriesList() = viewModelScope.launch {
        val exercises = getExerciseListUseCase().getOrElse { emptyList() }

        observeCategoriesListUseCase().collect { list ->
            updateIfForm {
                it.copy(
                    categories = listOf(null) + list,
                    items = exercises,
                )
            }
        }
    }

    private fun observeSavingChanges() = viewModelScope.launch {
        _state
            .filterIsInstance<TrainingPageUiState.TrainingForm>()
            .filter { it.hasUnsavedChanges && !it.isSaving }
            .map { it.item }
            .distinctUntilChanged()
            .debounce(2000)
            .onEach { showMessage(SavingProcess(), null) }
            .collectLatest {
                updateIfForm { it.copy(isSaving = true) }
                saveTraining {
                    updateIfForm {
                        it.copy(
                            hasUnsavedChanges = false,
                            isSaving = false
                        )
                    }
                }
            }
    }

    private fun observeTrainingEntity() = viewModelScope.launch {
        trainingId
            .flatMapLatest { id ->
                when (id) {
                    null -> flow {
                        val result = getNewTrainingUseCase(date)
                        emit(result.getOrThrow())
                    }

                    else -> getTrainingItemUseCase(id)

                }
            }
            .onEach { entity ->
                _state.update { currentState ->
                    when (currentState) {
                        is TrainingPageUiState.Error -> TrainingPageUiState.TrainingForm(item = entity)
                        TrainingPageUiState.Loading -> TrainingPageUiState.TrainingForm(item = entity)
                        is TrainingPageUiState.TrainingForm -> currentState.copy(item = entity)
                        else -> currentState
                    }
                }
            }
            .catch { error ->
                _state.update {
                    TrainingPageUiState.Error(
                        errorMessage = error.message ?: "Unknown error"
                    )
                }
            }
            .collect()
    }

    fun onEvent(event: TrainingPageEvent) {
        when (event) {
            is TrainingPageEvent.OnSelectedCategory -> onSelectedCategory(event.category)

            TrainingPageEvent.Save -> viewModelScope.launch {
                saveTraining {
                    _state.value = TrainingPageUiState.Success
                }
            }

            TrainingPageEvent.AddExercise -> addExercises()

            is TrainingPageEvent.OnCreateCategory -> createCategory()

            is TrainingPageEvent.OpenDialog -> openDialog(event.id)

            is TrainingPageEvent.OpenDialogGuide -> openDialogGuide(event.title, event.descr)


            TrainingPageEvent.OpenBottomSheetCategory -> updateIfForm { form ->
                form.copy(
                    bottomSheetCategoryStatus = form.bottomSheetCategoryStatus
                        ?: BottomSheetCategoryData(),
                    bottomSheetTrainingQuery = null,
                    items = if (form.bottomSheetCategoryStatus != null) form.items
                    else form.items.map { it.copy(checked = false) }
                )
            }

            TrainingPageEvent.OpenBottomSheetTraining -> updateIfForm { form ->
                form.copy(
                    bottomSheetTrainingQuery = "",
                    bottomSheetCategoryStatus = null,
                    items = form.items.map { it.copy(checked = false) }
                )
            }

            is TrainingPageEvent.Toggle -> updateIfForm { categoryForm ->
                categoryForm.copy(
                    items = categoryForm.items.map {
                        if (it.id == event.id) it.copy(checked = !it.checked)
                        else it
                    }
                )
            }

            is TrainingPageEvent.CalendarState -> updateIfForm { it.copy(calendarVisible = event.state) }

            TrainingPageEvent.OnErrorShown -> updateIfForm { it.copy(errorMessage = null) }

            is TrainingPageEvent.OnNameChange -> updateIfForm {
                if (it.bottomSheetCategoryStatus != null) it.copy(
                    bottomSheetCategoryStatus = it.bottomSheetCategoryStatus.copy(
                        categoryName = event.s,
                        isError = false
                    )
                ) else it
            }

            is TrainingPageEvent.OnQueryChange -> updateIfForm {
                when {
                    it.bottomSheetCategoryStatus != null -> it.copy(
                        bottomSheetCategoryStatus = it.bottomSheetCategoryStatus.copy(
                            query = event.s
                        )
                    )

                    else -> it.copy(bottomSheetTrainingQuery = event.s)
                }
            }

            is TrainingPageEvent.SortParamChange -> updateIfForm {
                val updatedList = it.modes.map { mode ->
                    if (mode == event.mode) {
                        mode.apply(event.param as SortParam)
                    } else {
                        mode
                    }
                }
                it.copy(modes = updatedList)
            }

            is TrainingPageEvent.Undoable -> {
                val action = when (val a = event.action) {
                    is TrainingUndoableAction.DeleteSet -> {
                        val form = _state.value as? TrainingPageUiState.TrainingForm
                        val snapshot = form?.item?.items
                            ?.find { it.id == a.exerciseId }
                            ?.items?.getOrNull(a.index)
                        if (snapshot != null) a.copy(snapshot = snapshot) else return
                    }

                    else -> a
                }
                handleUndoable(action)
            }

            is TrainingPageEvent.Undo -> undo()

            is TrainingPageEvent.Redo -> redo()
        }
    }

    private fun handleUndoable(action: TrainingUndoableAction) {
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

    private fun createCategory() {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

        viewModelScope.launch {
            if (s.bottomSheetCategoryStatus != null) {
                when {
                    s.bottomSheetCategoryStatus.categoryName.isEmpty() -> showMessage(
                        EmptyTitleError(),
                        true
                    )

                    checkNameCategoryExistsUseCase(
                        s.bottomSheetCategoryStatus.categoryName,
                        0
                    ).getOrElse { true } -> showMessage(TitleExistedError(), true)

                    else -> {
                        val items = selectedIds().mapIndexed { index, item ->
                            DataTypeTrainings(
                                typeId = 0,
                                exerciseId = item,
                                orderIndex = index
                            )
                        }

                        insertCategoryUseCase(
                            item = TypeTraining(0, s.bottomSheetCategoryStatus.categoryName),
                            items = items
                        ).fold(
                            onSuccess = {
                                updateIfForm { form ->
                                    form.copy(
                                        bottomSheetCategoryStatus = null,
                                        bottomSheetTrainingQuery = "",
                                        items = form.items.map { it.copy(checked = false) }
                                    )
                                }
                            },
                            onFailure = { error ->
                                updateIfForm {
                                    it.copy(
                                        bottomSheetCategoryStatus = it.bottomSheetCategoryStatus?.copy(
                                            isError = true
                                        ),
                                        errorMessage = error.getErrorMessage()
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun onSelectedCategory(category: TypeTraining?) {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

        viewModelScope.launch {
            val title =
                getSerialNumOfCategoryUseCase(category?.id).getOrElse { s.item.id.toString() }
            val list = when (category) {
                null -> emptyList()
                else -> getExercisesByCategoryUseCase(category.id).getOrElse { emptyList() }
            }

            onEvent(
                TrainingPageEvent.Undoable(
                    TrainingUndoableAction.OnSelectedCategory(
                        s.item, s.item.copy(
                            items = list,
                            title = title,
                            category = category
                        )
                    )
                )
            )
        }
    }

    private fun addExercises() {
        onEvent(
            TrainingPageEvent.Undoable(
                TrainingUndoableAction.OnAddExercises(
                    selectedIds()
                )
            )
        )
    }

    private suspend fun saveTraining(function: () -> Unit) {
        val s = _state.value as? TrainingPageUiState.TrainingForm ?: return

        upsertTrainingUseCase(s.item).fold(
            onSuccess = { id ->
                updateIfForm { it.copy(errorMessage = SavedChanges().getErrorMessage()) }
                trainingId.update { id }
                function()
            },
            onFailure = { error ->
                _state.value = TrainingPageUiState.Error(error.toString())
            }
        )
    }

    private fun selectedIds(): List<Int> {
        val currentState = _state.value as? TrainingPageUiState.TrainingForm ?: return listOf()
        return currentState.items.filter { it.checked }.map { it.id }
    }

    private fun openDialogGuide(title: String, description: String) = updateIfForm {
        it.copy(
            dialogContent = ExerciseDialogContent(
                id = -1,
                name = title,
                description = description,
            )
        )
    }

    private fun openDialog(id: Int?) {
        when (id) {
            null -> updateIfForm { it.copy(dialogContent = null) }
            else -> {
                viewModelScope.launch {
                    getExerciseDescriptionUseCase(id).fold(
                        onSuccess = { exercise ->
                            updateIfForm { it.copy(dialogContent = exercise) }
                        },
                        onFailure = {
                            showMessage(it, null)
                        }
                    )
                }
            }
        }
    }

    private fun showMessage(message: Throwable?, isError: Boolean?) = updateIfForm {
        it.copy(
            errorMessage = message?.getErrorMessage(),
            bottomSheetCategoryStatus = it.bottomSheetCategoryStatus?.copy(
                isError = isError ?: it.bottomSheetCategoryStatus.isError
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