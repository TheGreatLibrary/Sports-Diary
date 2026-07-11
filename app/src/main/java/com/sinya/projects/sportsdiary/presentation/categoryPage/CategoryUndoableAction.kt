package com.sinya.projects.sportsdiary.presentation.categoryPage

import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItemWithoutList
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.model.UndoableAction

sealed interface CategoryUndoableAction : UndoableAction<CategoryPageUiState.CategoryForm> {
    override fun apply(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm
    override fun revert(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm

    data class MoveExercise(val from: Int, val to: Int) : CategoryUndoableAction {
        override fun apply(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm {
            val mutable = state.item.items.toMutableList()
            if (from !in mutable.indices || from == to) return state
            val moved = mutable.removeAt(from)
            mutable.add(to.coerceIn(0, mutable.size), moved)
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = mutable.mapIndexed { i, item -> item.copy(orderIndex = i) }
                ))
        }

        override fun revert(state: CategoryPageUiState.CategoryForm) =
            MoveExercise(from = to, to = from).apply(state)
    }

    data class OnValueChange(
        val oldValue: String,
        val value: String
    ) : CategoryUndoableAction {
        override fun apply(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm {
            return state.copy(
                hasUnsavedChanges = true,
                isError = false,
                item = state.item.copy(
                    category = state.item.category.copy(name = value)
                )
            )
        }

        override fun revert(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm {
            return OnValueChange(value, oldValue).apply(state)
        }
    }
    data class DeleteExercise(val exercise: ExerciseItemWithoutList, val index: Int) : CategoryUndoableAction {
        override fun apply(state: CategoryPageUiState.CategoryForm) =
            state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items
                        .filterNot { it.id == exercise.id }
                        .mapIndexed { i, item -> item.copy(orderIndex = i) }
                )
            )

        override fun revert(state: CategoryPageUiState.CategoryForm) =
            state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.toMutableList().apply {
                        val safeIndex = index.coerceIn(0, size)
                        add(safeIndex, exercise)
                    }.mapIndexed { i, item -> item.copy(orderIndex = i) }
                )
            )
    }

    data class ToggleMode(val exerciseId: Int, val mode: Int) :
        CategoryUndoableAction {
        override fun apply(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { item ->
                        if (item.id == exerciseId) item.copy(mode = if (mode == 0) 1 else 0)
                        else item
                    }
                )
            )
        }


        override fun revert(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm {
            return ToggleMode(exerciseId, if (mode == 0) 1 else 0).apply(state)
        }

    }

    data class OnAddExercises(val list: List<Int>) : CategoryUndoableAction {
        override fun apply(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm {

            fun selectedIds(): List<ExerciseWithMuscles> {
                return state.sheetData.items.filter { it.id in list }
            }

            val items = selectedIds().mapIndexed { index, it ->
                ExerciseItemWithoutList(
                    id = it.id,
                    title = it.name,
                    mode = 0,
                    weight = 0,
                    orderIndex = index
                )
            }

            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = (state.item.items + items).distinctBy { it.id }.mapIndexed { index, item ->
                        item.copy(orderIndex = index)
                    }.toList()
                ),
                sheetData = state.sheetData.copy(
                    query = "",
                    items = state.sheetData.items.map { it.copy(checked = false) }
                )
            )
        }

        override fun revert(state: CategoryPageUiState.CategoryForm): CategoryPageUiState.CategoryForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = (state.item.items.filter { it.id !in list }.mapIndexed { index, item ->
                        item.copy(orderIndex = index)
                        }
                    )
                )
            )
        }
    }
}