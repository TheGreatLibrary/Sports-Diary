package com.sinya.projects.sportsdiary.presentation.categoryPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.core.domain.error.EmptyTitleError
import com.sinya.projects.sportsdiary.core.domain.error.SavedChanges
import com.sinya.projects.sportsdiary.core.domain.error.SavingProcess
import com.sinya.projects.sportsdiary.core.domain.error.TitleExistedError
import com.sinya.projects.sportsdiary.core.domain.model.CategorySheetItem
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.model.SortParam
import com.sinya.projects.sportsdiary.core.domain.useCase.CheckNameCategoryExistsUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExerciseWithSortedDataUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.InsertCategoryWithDataUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.ObserveCategoryEntityUseCase
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel(assistedFactory = CategoryPageViewModel.Factory::class)
class CategoryPageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,

    private val observeCategoryEntityUseCase: ObserveCategoryEntityUseCase,
    private val getExerciseListUseCase: GetExerciseWithSortedDataUseCase,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,

    private val insertCategoryUseCase: InsertCategoryWithDataUseCase,
    private val checkNameCategoryExistsUseCase: CheckNameCategoryExistsUseCase
) : ViewModel() {

    private val undoManager = UndoManager<CategoryPageUiState.CategoryForm>()
    private var undoJob: Job? = null

    private val _state = MutableStateFlow<CategoryPageUiState>(CategoryPageUiState.Loading)
    val state: StateFlow<CategoryPageUiState> = _state.asStateFlow()

    @OptIn(FlowPreview::class)
    val filteredList: StateFlow<List<ExerciseWithMuscles>> = state
        .map { currentState ->
            when (currentState) {
                is CategoryPageUiState.CategoryForm -> {
                    val filtered =
                        currentState.sheetData.modes.fold(currentState.sheetData.items) { exercises, mode ->
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
        ): CategoryPageViewModel
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
                        is CategoryPageUiState.Loading,
                        is CategoryPageUiState.Error ->
                            CategoryPageUiState.CategoryForm(
                                item = entity,
                                sheetData = CategorySheetItem("", exercises),
                            )

                        is CategoryPageUiState.CategoryForm ->
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
                    CategoryPageUiState.Error(
                        errorMessage = error.message ?: "Unknown error"
                    )
                }
            }
            .collect()
    }
    private fun observeSavingChanges() = viewModelScope.launch {
        _state
            .filterIsInstance<CategoryPageUiState.CategoryForm>()
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

    fun onEvent(event: CategoryPageEvent) {
        when (event) {
            CategoryPageEvent.OnErrorShown -> showMessage(null, null)

            is CategoryPageEvent.OpenDialog -> openDialog(event.id)

            CategoryPageEvent.Save -> viewModelScope.launch {
                saveCategory { _state.update { CategoryPageUiState.Success } }
            }

            is CategoryPageEvent.AddExercise -> addExercisesToCategory()

            is CategoryPageEvent.Toggle -> updateIfForm { it ->
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

            is CategoryPageEvent.SortParamChange -> updateIfForm {
                val updatedList = it.sheetData.modes.map { mode ->
                    when {
                        mode == event.mode -> mode.apply(event.param as SortParam)
                        else -> mode
                    }
                }
                it.copy(
                    sheetData = it.sheetData.copy(
                        modes = updatedList
                    )
                )
            }

            is CategoryPageEvent.OnQueryChange -> updateIfForm {
                it.copy(sheetData = it.sheetData.copy(query = event.name))
            }

            is CategoryPageEvent.Undoable -> handleUndoable(event.action)

            is CategoryPageEvent.Undo -> undo()

            is CategoryPageEvent.Redo -> redo()
        }
    }

    private fun handleUndoable(action: CategoryUndoableAction) {
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
        val s = _state.value as? CategoryPageUiState.CategoryForm ?: return

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
            CategoryPageEvent.Undoable(
                CategoryUndoableAction.OnAddExercises(
                    selectedIds()
                )
            )
        )
    }
    private fun saveCategory(function: () -> Unit) {
        val s = _state.value as? CategoryPageUiState.CategoryForm ?: return

        checkTitleCategory {
            val id = s.item.category.id
            val items = s.item.items.map {
                DataTypeTrainings(
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
        val currentState = _state.value as? CategoryPageUiState.CategoryForm ?: return listOf()
        return currentState.sheetData.items.filter { it.checked }.map { it.id }
    }

    private fun updateIfForm(transform: (CategoryPageUiState.CategoryForm) -> CategoryPageUiState.CategoryForm) {
        _state.update { currentState ->
            if (currentState is CategoryPageUiState.CategoryForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}