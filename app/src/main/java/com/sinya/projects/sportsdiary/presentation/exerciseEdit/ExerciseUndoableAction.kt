package com.sinya.projects.sportsdiary.presentation.exerciseEdit

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ForceTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.LevelTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.core.domain.enums.ExerciseMode
import com.sinya.projects.sportsdiary.core.domain.model.UndoableAction

sealed interface ExerciseUndoableAction : UndoableAction<ExerciseEditUiState.ExerciseForm> {
    override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm
    override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm

    data class OnTitleChange(
        val oldValue: String,
        val value: String
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(
                    name = value,
                    isError = false
                ),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnTitleChange(value, oldValue).apply(state)
        }
    }

    data class OnDescriptionChange(
        val oldValue: String,
        val value: String
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(
                    description = value,
                ),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnDescriptionChange(value, oldValue).apply(state)
        }
    }

    data class OnRuleChange(
        val oldValue: String,
        val value: String
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(
                    rule = value,
                ),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnRuleChange(value, oldValue).apply(state)
        }
    }



    data class OnSelectedCategory(
        val oldItem: CategoryTranslation?,
        val item: CategoryTranslation?
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(category = item),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnSelectedCategory(item, oldItem).apply(state)
        }
    }

    data class OnSelectedForce(
        val oldItem: ForceTranslation?,
        val item: ForceTranslation?
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(force = item),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnSelectedForce(item, oldItem).apply(state)
        }
    }

    data class OnSelectedMechanic(
        val oldItem: MechanicTranslation?,
        val item: MechanicTranslation?
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(mechanic = item),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnSelectedMechanic(item, oldItem).apply(state)
        }
    }

    data class OnSelectedEquipment(
        val oldItem: EquipmentTranslation?,
        val item: EquipmentTranslation?
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(equipment = item),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnSelectedEquipment(item, oldItem).apply(state)
        }
    }

    data class OnSelectedLevel(
        val oldItem: LevelTranslation?,
        val item: LevelTranslation?
    ) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                exercise = state.exercise.copy(level = item),
                hasUnsavedChanges = true
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return OnSelectedLevel(item, oldItem).apply(state)
        }
    }

    data class ToggleMuscleValue(val muscleId: Int, val oldValue: ExerciseMode, val value: ExerciseMode) : ExerciseUndoableAction {
        override fun apply(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return state.copy(
                hasUnsavedChanges = true,
                muscles = state.muscles.map {
                    when(it.muscleId == muscleId) {
                        true -> it.copy(
                            value = value.code,
                            checked = when(value) {
                                ExerciseMode.MAIN, ExerciseMode.SIDE -> true
                                ExerciseMode.NONE -> false
                            }
                        )
                        false -> it
                    }
                }
            )
        }

        override fun revert(state: ExerciseEditUiState.ExerciseForm): ExerciseEditUiState.ExerciseForm {
            return ToggleMuscleValue(muscleId, value, oldValue).apply(state)
        }
    }
}

