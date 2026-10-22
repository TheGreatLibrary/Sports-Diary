package com.sinya.projects.editexercise.subScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.editexercise.ExerciseEditEvent
import com.sinya.projects.editexercise.ExerciseEditUiState
import com.sinya.projects.editexercise.ExerciseUndoableAction
import com.sinya.projects.editexercise.components.SwipeableTimelineSelector
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.dropdown.DropdownCheckboxMenu
import com.sinya.projects.ui.components.dropdown.components.DropdownElement

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
internal fun CharacteristicsScreen(
    state: ExerciseEditUiState.ExerciseForm,
    onEvent: (ExerciseEditEvent) -> Unit
) {
    val context = LocalContext.current
    val category = stringResource(R.string.category)
    val categoryDescription = stringResource(R.string.category_desc)
    val force = stringResource(R.string.force)
    val mechanic = stringResource(R.string.mechanic)
    val equipment = stringResource(R.string.equipment)
    val equipmentDescription = stringResource(R.string.equipment_desc)
    val level = stringResource(R.string.level)
    val notCategory = stringResource(R.string.not_category)
    var categoryExpanded by remember { mutableStateOf(false) }
    var equipmentExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DropdownCheckboxMenu(
            items = state.categories,
            title = category,
            onInfoClick = {
                onEvent(ExerciseEditEvent.OpenDialogGuide(category, categoryDescription))
            },
            selectedItems = listOf(state.exercise.category),
            expanded = categoryExpanded,
            onExpanded = { categoryExpanded = it },
            nameObject = { list -> list.joinToString { it?.name ?: notCategory } },
            onPlusClick = null
        ) { item ->
            DropdownElement(
                title = item?.name ?: notCategory,
                onClick = {
                    onEvent(
                        ExerciseEditEvent.Undoable(
                            ExerciseUndoableAction.OnSelectedCategory(
                                state.exercise.category,
                                item
                            )
                        )
                    )
                    categoryExpanded = false
                }
            )
        }

        DropdownCheckboxMenu(
            items = state.equipments,
            title = equipment,
            onInfoClick = {
                onEvent(ExerciseEditEvent.OpenDialogGuide(equipment, equipmentDescription))
            },
            selectedItems = listOf(state.exercise.equipment),
            expanded = equipmentExpanded,
            onExpanded = { equipmentExpanded = it },
            nameObject = { list -> list.joinToString { it?.name ?: notCategory } },
            onPlusClick = null
        ) { item ->
            DropdownElement(
                title = item?.name ?: notCategory,
                onClick = {
                    onEvent(
                        ExerciseEditEvent.Undoable(
                            ExerciseUndoableAction.OnSelectedEquipment(
                                state.exercise.equipment,
                                item
                            )
                        )
                    )
                    equipmentExpanded = false
                }
            )
        }

        Text(
            text = mechanic, // Заголовок над свайпером
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )

        SwipeableTimelineSelector(
            items = state.mechanics,
            selectedItem = state.exercise.mechanic,
            nameSelector = { it?.name ?: context.getString(R.string.not_category) },
            onItemSelected = { selectedMechanic ->
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedMechanic(
                            state.exercise.mechanic,
                            selectedMechanic
                        )
                    )
                )
            }
        )

        Text(
            text = force,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )

        SwipeableTimelineSelector(
            items = state.forces,
            selectedItem = state.exercise.force,
            nameSelector = { it?.name ?: context.getString(R.string.not_category) },
            onItemSelected = { selectedMechanic ->
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedForce(
                            state.exercise.force,
                            selectedMechanic
                        )
                    )
                )
            }
        )

        Text(
            text = level, // Заголовок над свайпером
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )

        SwipeableTimelineSelector(
            items = state.levels,
            selectedItem = state.exercise.level,
            nameSelector = { it?.name ?: context.getString(R.string.not_category) },
            onItemSelected = { selectedMechanic ->
                onEvent(
                    ExerciseEditEvent.Undoable(
                        ExerciseUndoableAction.OnSelectedLevel(
                            state.exercise.level,
                            selectedMechanic
                        )
                    )
                )
            }
        )
    }
}