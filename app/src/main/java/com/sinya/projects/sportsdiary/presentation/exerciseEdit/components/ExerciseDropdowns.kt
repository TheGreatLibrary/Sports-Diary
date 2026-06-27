package com.sinya.projects.sportsdiary.presentation.exerciseEdit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.ExerciseEditEvent
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.ExerciseEditUiState
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.ExerciseUndoableAction
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomDropdownMenu

@Composable
fun ExerciseDropdowns(
    state: ExerciseEditUiState.ExerciseForm,
    onEvent: (ExerciseEditEvent) -> Unit
) {
    val category = stringResource(R.string.category)
    val categoryDescription = stringResource(R.string.category_desc)
    val force = stringResource(R.string.force)
    val forceDescription = stringResource(R.string.force_desc)
    val mechanic = stringResource(R.string.mechanic)
    val mechanicDescription = stringResource(R.string.mechanic_desc)
    val equipment = stringResource(R.string.equipment)
    val equipmentDescription = stringResource(R.string.equipment_desc)
    val level = stringResource(R.string.level)
    val levelDescriptionUse = stringResource(R.string.level_desc)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CustomDropdownMenu(
            items = state.categories,
            title = category,
            onInfoClick = {
                onEvent(
                    ExerciseEditEvent.OpenDialogGuide(
                        category,
                        categoryDescription
                    )
                )
            },
            selectedItem = state.exercise.category,
            nameObject = { it.name },
            onSelectedCategory = {
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedCategory(
                            state.exercise.category,
                            it
                        )
                    )
                )
            },
            onPlusClick = null
        )

        CustomDropdownMenu(
            items = state.forces,
            title = force,
            onInfoClick = { onEvent(ExerciseEditEvent.OpenDialogGuide(force, forceDescription)) },
            selectedItem = state.exercise.force,
            nameObject = { it.name },
            onSelectedCategory = {
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedForce(
                            state.exercise.force,
                            it
                        )
                    )
                )
            },
            onPlusClick = null
        )

        CustomDropdownMenu(
            items = state.levels,
            title = level,
            onInfoClick = {
                onEvent(
                    ExerciseEditEvent.OpenDialogGuide(
                        level,
                        levelDescriptionUse
                    )
                )
            },
            selectedItem = state.exercise.level,
            nameObject = { it.name },
            onSelectedCategory = {
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedLevel(
                            state.exercise.level,
                            it
                        )
                    )
                )
            },
            onPlusClick = null
        )

        CustomDropdownMenu(
            items = state.mechanics,
            title = mechanic,
            onInfoClick = {
                onEvent(
                    ExerciseEditEvent.OpenDialogGuide(
                        mechanic,
                        mechanicDescription
                    )
                )
            },
            selectedItem = state.exercise.mechanic,
            nameObject = { it.name },
            onSelectedCategory = {
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedMechanic(
                            state.exercise.mechanic,
                            it
                        )
                    )
                )
            },
            onPlusClick = null
        )

        CustomDropdownMenu(
            items = state.equipments,
            title = equipment,
            onInfoClick = {
                onEvent(
                    ExerciseEditEvent.OpenDialogGuide(
                        equipment,
                        equipmentDescription
                    )
                )
            },
            selectedItem = state.exercise.equipment,
            nameObject = { it.name },
            onSelectedCategory = {
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedEquipment(
                            state.exercise.equipment,
                            it
                        )
                    )
                )
            },
            onPlusClick = null
        )
    }
}