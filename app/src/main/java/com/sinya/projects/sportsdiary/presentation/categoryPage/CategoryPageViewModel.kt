package com.sinya.projects.sportsdiary.presentation.categoryPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.domain.model.CategorySheetItem
import com.sinya.projects.sportsdiary.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.domain.useCase.GetCategoryItemUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetExerciseListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.InsertCategoryWithDataUseCase
import com.sinya.projects.sportsdiary.domain.useCase.UpdateCategoryDataUseCase
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.utils.searchByTerms
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CategoryPageViewModel.Factory::class)
class CategoryPageViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,

    private val getCategoryItemUseCase: GetCategoryItemUseCase,
    private val insertCategoryUseCase: InsertCategoryWithDataUseCase,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,
    private val updateCategoryDataUseCase: UpdateCategoryDataUseCase,
    private val getExerciseListUseCase: GetExerciseListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CategoryPageUiState>(CategoryPageUiState.Loading)
    val state: StateFlow<CategoryPageUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): CategoryPageViewModel
    }

    init {
        loadData(id)
    }

    fun onEvent(event: CategoryPageEvent) {
        when (event) {
            is CategoryPageEvent.Delete -> deleteExercise(event.id)

            is CategoryPageEvent.MoveExercise -> moveExercise(event.from, event.to)

            CategoryPageEvent.OnErrorShown -> updateIfForm { it.copy(errorMessage = null) }

            is CategoryPageEvent.OnValueChange -> updateIfForm {
                it.copy(
                    item = it.item.copy(
                        category = it.item.category.copy(name = event.name)
                    )
                )
            }

            is CategoryPageEvent.OpenBottomSheetTraining -> saveCategory {
                updateIfForm { it.copy(bottomSheetCategoryItemsState = event.state) }
            }

            is CategoryPageEvent.OpenDialog -> openDialog(event.id)

            CategoryPageEvent.Save -> saveCategory {
                _state.value = CategoryPageUiState.Success
            }

            is CategoryPageEvent.AddExercise -> saveCategory {
                updateCategoryData { it -> getCategoryEntity(it)}
            }

            is CategoryPageEvent.Toggle -> {
                updateIfForm {
                    it.copy(
                        sheetData = it.sheetData.copy(
                            items = it.sheetData.items.map { if (it.id == event.id) it.copy(checked = !it.checked) else it }
                        )
                    )
                }
            }

            is CategoryPageEvent.UpdateListTraining -> getCategoryEntity(event.id)

            is CategoryPageEvent.OnQueryChange -> {
                updateIfForm {
                    it.copy(
                        sheetData = it.sheetData.copy(
                            query = event.name
                        )
                    )
                }
            }
        }
    }

    private fun loadData(id: Int?) = viewModelScope.launch {
        getCategoryEntity(id)
    }

    fun filtered(): List<ExerciseUi> {
        val currentState = _state.value as? CategoryPageUiState.CategoryForm ?: return listOf()
        return currentState.sheetData.items.searchByTerms(currentState.sheetData.query) { it.name }
    }

    private fun getCategorySheetData() = viewModelScope.launch {
        val items = getExerciseListUseCase.invoke()
        updateIfForm {
            it.copy(
                sheetData = CategorySheetItem(
                    query = "",
                    items = items
                )
            )
        }
    }

    private fun updateCategoryData(function: (Int) -> Unit) = viewModelScope.launch {
        val s = _state.value as? CategoryPageUiState.CategoryForm ?: return@launch
        val items1 = s.sheetData.items.filter { it.checked }.mapIndexed { index, it ->
            DataTypeTrainings(
                s.item.category.id,
                it.id,
                index
            )
        }
        Log.d("DD", items1.toString())

        updateCategoryDataUseCase(items1).fold(
            onSuccess = {
                Log.d("DD", "Success")
                updateIfForm {
                    it.copy(bottomSheetCategoryItemsState = false)
                }
                function(s.item.category.id)
            },
            onFailure = { error ->
                _state.value = CategoryPageUiState.Error(errorMessage = error.toString())
            }
        )
    }

    private fun getCategoryEntity(id: Int? = this.id) = viewModelScope.launch {
        val items = getExerciseListUseCase.invoke()

        getCategoryItemUseCase(id).fold(
            onSuccess = { item ->
                if (_state.value is CategoryPageUiState.CategoryForm) {
                    updateIfForm {
                        it.copy(
                            item = item,
                            sheetData = CategorySheetItem("", items),
                            bottomSheetCategoryItemsState = false,
                            dialogContent = null,
                            errorMessage = null
                        )
                    }
                } else {
                    _state.value = CategoryPageUiState.CategoryForm(
                        item = item,
                        sheetData = CategorySheetItem("", items)
                    )
                }
            },
            onFailure = { error ->
                _state.value = CategoryPageUiState.Error(errorMessage = error.toString())
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

    private fun saveCategory(function: () -> Unit) = viewModelScope.launch {
        val s = _state.value as? CategoryPageUiState.CategoryForm ?: return@launch
        val id = s.item.category.id
        val items = s.item.items.map {
            DataTypeTrainings(
                typeId = id,
                exerciseId = it.id,
                orderIndex = it.orderIndex
            )
        }

        insertCategoryUseCase(s.item.category, items).fold(
            onSuccess = { id1 ->
                updateIfForm { it.copy(item = it.item.copy(category = it.item.category.copy(id = id1))) }
                function()
            },
            onFailure = { error ->
                _state.value = CategoryPageUiState.Error(error.toString())
            }
        )
    }

    private fun deleteExercise(exerciseId: Int?) {
        updateIfForm {
            it.copy(item = it.item.copy(
                items = it.item.items.filterNot { item -> item.id == exerciseId }
                )
            )
        }
    }

    private fun moveExercise(from: Int, to: Int) {
        val s = _state.value as? CategoryPageUiState.CategoryForm ?: return

        updateIfForm {
            val mutable = s.item.items.toMutableList()

            if (from !in mutable.indices) return@updateIfForm it
            if (from == to) return@updateIfForm it

            val moved = mutable.removeAt(from)

            val target = to.coerceIn(0, mutable.size)
            mutable.add(target, moved)


            it.copy(
                item = it.item.copy(
                    items = mutable.mapIndexed { index, item ->
                        item.copy(orderIndex = index)
                    }
                )
            )
        }
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