package com.sinya.projects.pagecategory.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.error.EmptyTitleError
import com.sinya.projects.domain.error.SavedChanges
import com.sinya.projects.domain.error.SavingProcess
import com.sinya.projects.domain.error.TitleExistedError
import com.sinya.projects.domain.model.CategorySheetItem
import com.sinya.projects.domain.model.DataTypeTrainingsEntry
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.useCase.CheckNameCategoryExistsUseCase
import com.sinya.projects.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.domain.useCase.GetExerciseWithSortedDataUseCase
import com.sinya.projects.domain.useCase.InsertCategoryWithDataUseCase
import com.sinya.projects.domain.useCase.ObserveCategoryEntityUseCase
import com.sinya.projects.domain.utils.UndoManager
import com.sinya.projects.domain.utils.getErrorMessage
import com.sinya.projects.domain.utils.searchByTerms
import com.sinya.projects.ui.components.sort.SortParam
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel(assistedFactory = RoutinePageViewModel.Factory::class)
internal class RoutinePageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,

    private val observeCategoryEntityUseCase: ObserveCategoryEntityUseCase,
    private val getExerciseListUseCase: GetExerciseWithSortedDataUseCase,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,

    private val insertCategoryUseCase: InsertCategoryWithDataUseCase,
    private val checkNameCategoryExistsUseCase: CheckNameCategoryExistsUseCase
) : ViewModel() {

    private val undoManager = UndoManager<RoutinePageUiState.RoutineForm>()
    private var undoJob: Job? = null

    private val _state = MutableStateFlow<RoutinePageUiState>(RoutinePageUiState.Loading)
    val state: StateFlow<RoutinePageUiState> = _state.asStateFlow()

    @OptIn(FlowPreview::class)
    val filteredList: StateFlow<List<ExerciseWithMuscles>> = state
        .map { currentState ->
            when (currentState) {
                is RoutinePageUiState.RoutineForm -> {
                    val filtered =
                        currentState.modes.fold(currentState.sheetData.items) { exercises, mode ->
                            mode.filter(exercises)
                        }
                    filtered.searchByTerms(currentState.sheetData.query, { it.name })
                }

                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val categoryId = MutableStateFlow(id)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): RoutinePageViewModel
    }

    init {
        observeCategoryEntity()
        observeSavingChanges()
    }

    private fun observeCategoryEntity() = viewModelScope.launch {
        val exercises = getExerciseListUseCase().getOrElse { emptyList() }

        categoryId
            .flatMapLatest { id ->
                observeCategoryEntityUseCase(id)
            }
            .onEach { entity ->
                _state.update { currentState ->
                    when (currentState) {
                        is RoutinePageUiState.Loading,
                        is RoutinePageUiState.Error ->
                            RoutinePageUiState.RoutineForm(
                                item = entity,
                                sheetData = CategorySheetItem("", exercises),
                            )

                        is RoutinePageUiState.RoutineForm ->
                            currentState.copy(
                                item = entity,
                                sheetData = CategorySheetItem("", exercises),
                                dialogContent = null,
                            )

                        else -> currentState
                    }
                }
            }
            .catch { error ->
                _state.update {
                    RoutinePageUiState.Error(
                        errorMessage = error.message ?: "Unknown error"
                    )
                }
            }
            .collect()
    }
    private fun observeSavingChanges() = viewModelScope.launch {
        _state
            .filterIsInstance<RoutinePageUiState.RoutineForm>()
            .filter { it.hasUnsavedChanges && !it.isSaving }
            .map { it.item }
            .distinctUntilChanged()
            .filter { it.category.name.isNotBlank() }
            .debounce(2000)
            .onEach { showMessage(null, SavingProcess()) }
            .collectLatest {
                updateIfForm { it.copy(isSaving = true) }
                saveCategory {
                    updateIfForm {
                        it.copy(
                            hasUnsavedChanges = false,
                            isSaving = false
                        )
                    }
                }
            }
    }


    fun onEvent(event: RoutinePageEvent) {
        when (event) {
            RoutinePageEvent.OnErrorShown -> showMessage(null, null)

            is RoutinePageEvent.OpenDialog -> openDialog(event.id)

            RoutinePageEvent.Save -> viewModelScope.launch {
                saveCategory { _state.update { RoutinePageUiState.Success } }
            }

            is RoutinePageEvent.AddExercise -> addExercisesToCategory()

            is RoutinePageEvent.Toggle -> updateIfForm { it ->
                val updatedItems = it.sheetData.items.map {
                    when {
                        it.id == event.id -> it.copy(checked = !it.checked)
                        else -> it
                    }
                }

                it.copy(
                    sheetData = it.sheetData.copy(
                        items = updatedItems
                    )
                )
            }

            is RoutinePageEvent.SortParamChange -> updateIfForm {
                val updatedList = it.modes.map { mode ->
                    when {
                        mode == event.mode -> mode.apply(event.param as SortParam)
                        else -> mode
                    }
                }
                it.copy(modes = updatedList)
            }

            is RoutinePageEvent.OnQueryChange -> updateIfForm {
                it.copy(sheetData = it.sheetData.copy(query = event.name))
            }

            is RoutinePageEvent.Undoable -> handleUndoable(event.action)

            is RoutinePageEvent.Undo -> undo()

            is RoutinePageEvent.Redo -> redo()
        }
    }

    private fun handleUndoable(action: RoutineUndoableAction) {
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

    private fun checkTitleCategory(function: () -> Unit) {
        val s = _state.value as? RoutinePageUiState.RoutineForm ?: return

        viewModelScope.launch {
            when {
                s.item.category.name.isEmpty() -> showMessage(true, EmptyTitleError())

                checkNameCategoryExistsUseCase(
                    s.item.category.name,
                    s.item.category.id
                ).getOrElse { true } -> showMessage(true, TitleExistedError())

                else -> function()
            }
        }
    }

    private fun addExercisesToCategory() {
        onEvent(
            RoutinePageEvent.Undoable(
                RoutineUndoableAction.OnAddExercises(
                    selectedIds()
                )
            )
        )
    }
    private fun saveCategory(function: () -> Unit) {
        val s = _state.value as? RoutinePageUiState.RoutineForm ?: return

        checkTitleCategory {
            val id = s.item.category.id
            val items = s.item.items.map {
                DataTypeTrainingsEntry(
                    typeId = id,
                    exerciseId = it.id,
                    mode = it.mode,
                    orderIndex = it.orderIndex
                )
            }

            viewModelScope.launch {
                insertCategoryUseCase(s.item.category, items).fold(
                    onSuccess = { id1 ->
                        updateIfForm {
                            it.copy(
                                item = it.item.copy(category = it.item.category.copy(id = id1)),
                                errorMessage = SavedChanges().getErrorMessage()
                            )
                        }
                        categoryId.update { id1 }
                        function()
                    },
                    onFailure = { error -> showMessage(true, error) }
                )
            }
        }
    }

    private fun showMessage(isError: Boolean?, message: Throwable?) = updateIfForm {
        it.copy(
            isError = isError ?: it.isError,
            errorMessage = message?.getErrorMessage()
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
                            showMessage(null, it)
                        }
                    )
                }
            }
        }
    }

    private fun selectedIds(): List<Int> {
        val currentState = _state.value as? RoutinePageUiState.RoutineForm ?: return listOf()
        return currentState.sheetData.items.filter { it.checked }.map { it.id }
    }

    private fun updateIfForm(transform: (RoutinePageUiState.RoutineForm) -> RoutinePageUiState.RoutineForm) {
        _state.update { currentState ->
            if (currentState is RoutinePageUiState.RoutineForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}