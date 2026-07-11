package com.sinya.projects.sportsdiary.presentation.trainingPage

import com.sinya.projects.sportsdiary.core.domain.enums.TrainingMode
import com.sinya.projects.sportsdiary.core.domain.enums.Visibility
import com.sinya.projects.sportsdiary.core.domain.enums.WeightState
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseData
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItem
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItemData
import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.core.domain.model.UndoableAction
import com.sinya.projects.sportsdiary.core.domain.model.addEmptySet
import com.sinya.projects.sportsdiary.core.domain.model.insertSet
import com.sinya.projects.sportsdiary.core.domain.model.removeSet
import com.sinya.projects.sportsdiary.core.domain.model.toItem
import com.sinya.projects.sportsdiary.core.domain.model.updateSet

sealed interface TrainingUndoableAction : UndoableAction<TrainingPageUiState.TrainingForm> {
    override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm
    override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm

    data class MoveExercise(val from: Int, val to: Int) : TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
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

        override fun revert(state: TrainingPageUiState.TrainingForm) =
            MoveExercise(from = to, to = from).apply(state)
    }

    data class AddSet(val exerciseId: Int) : TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { ex ->
                        if (ex.id != exerciseId) ex
                        else ex.addEmptySet()
                    }
                )
            )
        }

        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { ex ->
                        if (ex.id != exerciseId) ex
                        else ex.removeSet(ex.items.lastIndex)
                    }
                )
            )
        }

    }

    data class DeleteSet(val exerciseId: Int, val index: Int, val snapshot: ExerciseItemData) :
        TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm =
            state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { ex ->
                        if (ex.id != exerciseId) ex else ex.removeSet(index)
                    }
                ))


        override fun revert(state: TrainingPageUiState.TrainingForm) =
            state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { ex ->
                        if (ex.id != exerciseId) ex else ex.insertSet(index, snapshot)
                    }
                ))
    }

    data class DeleteExercise(val exercise: ExerciseItem, val index: Int) : TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm) =
            state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items
                        .filterNot { it.id == exercise.id }
                        .mapIndexed { i, item -> item.copy(orderIndex = i) }
                )
            )

        override fun revert(state: TrainingPageUiState.TrainingForm) =
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

    data class EditSet(
        val exerciseId: Int,
        val index: Int,
        val oldValue: String?,
        val value: String?,
        val valState: Boolean
    ) : TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            val newItems = state.item.items.map { ex ->
                if (ex.id != exerciseId) ex
                else {
                    if (index !in ex.items.indices) ex
                    else {
                        if (valState) ex.updateSet(index, value, null)
                        else ex.updateSet(index, null, value)
                    }
                }
            }

            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(items = newItems)
            )
        }

        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return EditSet(exerciseId, index, value, oldValue, valState).apply(state)
        }

    }

    data class OnPickDate(val date: String, val oldDate: String) : TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    date = date
                )
            )
        }

        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return OnPickDate(oldDate, date).apply(state)
        }
    }

    data class VisibleExercise(val exerciseId: Int, val oldValue: Visibility, val value: Visibility) :
        TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { item ->
                        if (item.id == exerciseId) {
                            item.copy(state = value)
                        } else item
                    }
                )
            )
        }

        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return VisibleExercise(exerciseId, value, oldValue).apply(state)
        }

    }

    data class ToggleWeight(val exerciseId: Int, val oldValue: WeightState, val value: WeightState) :
        TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { item ->
                        if (item.id == exerciseId) {
                            item.copy(weightState = value)
                        } else item
                    }
                )
            )
        }

        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return ToggleWeight(exerciseId, value, oldValue).apply(state)
        }

    }

    data class ToggleMode(val exerciseId: Int, val oldMode: TrainingMode, val mode: TrainingMode) :
        TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = state.item.items.map { item ->
                        if (item.id == exerciseId) {
                            item.copy(mode = mode)
                        } else item
                    }
                )
            )
        }


        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return ToggleMode(exerciseId, mode, oldMode).apply(state)
        }

    }

    data class OnSelectedCategory(
        val oldTrainingEntity: TrainingEntity,
        val trainingEntity: TrainingEntity
    ) :
        TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return state.copy(
                hasUnsavedChanges = true,
                item = trainingEntity
            )
        }

        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
            return OnSelectedCategory(trainingEntity, oldTrainingEntity).apply(state)
        }
    }

    data class OnAddExercises(val list: List<Int>) : TrainingUndoableAction {
        override fun apply(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {

            fun selectedIds(): List<Pair<Int, String>> {
                return state.items.filter { it.id in list }.map { it.id to it.name }
            }

            val items = selectedIds().map { item ->
                ExerciseData(
                    id = item.first,
                    title = item.second,
                    state = 1,
                    weight = 0,
                    mode = TrainingMode.COUNT.mode,
                    orderIndex = 0,
                    countResult = "0/0/0/0",
                    weightResult = "0/0/0/0",
                    prevCountResult = "0/0/0/0",
                    prevWeightResult = "0/0/0/0",
                ).toItem()
            }

            return state.copy(
                hasUnsavedChanges = true,
                item = state.item.copy(
                    items = (state.item.items + items).distinctBy { it.id }.mapIndexed { index, item ->
                        item.copy(
                            orderIndex = index
                        )
                    }.toList()
                ),
                bottomSheetTrainingQuery = "",
                items = state.items.map { it.copy(checked = false) }
            )
        }

        override fun revert(state: TrainingPageUiState.TrainingForm): TrainingPageUiState.TrainingForm {
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