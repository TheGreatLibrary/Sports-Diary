package com.sinya.projects.pagetraining.form

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.enums.TrainingMode
import com.sinya.projects.domain.enums.Visibility
import com.sinya.projects.domain.enums.WeightState
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.model.PopupItem
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.domain.utils.parseMillisToDateTime
import com.sinya.projects.pagetraining.form.components.TrainingPagePlaceholder
import com.sinya.projects.pagetraining.form.components.card.SwipeTrainingCard
import com.sinya.projects.pagetraining.form.components.card.components.SwipeTrainingCardContent
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.DateCard
import com.sinya.projects.ui.components.PopupMenu
import com.sinya.projects.ui.components.dropdown.DropdownCheckboxMenu
import com.sinya.projects.ui.components.dropdown.components.DropdownElement
import com.sinya.projects.ui.components.reorderList.rememberReorderState
import com.sinya.projects.ui.components.screen.ScreenLazyColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.CategorySheetContent
import com.sinya.projects.ui.components.sheet.components.ExerciseSheetContent
import com.sinya.projects.ui.components.trainingConstructor.UndoBottomNavigation
import com.sinya.projects.ui.components.CustomButton
import com.sinya.projects.ui.components.CustomSnackbar
import com.sinya.projects.ui.components.DatePickerModal
import com.sinya.projects.ui.components.DragOverlay
import com.sinya.projects.ui.components.dialog.GuideDescriptionView
import com.sinya.projects.ui.components.dialog.GuideDialog
import kotlinx.coroutines.launch

@Composable
internal fun TrainingForm(
    onBackClick: () -> Unit,
    id: Int?,
    date: String?,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: TrainingFormViewModel.Factory ->
            factory.create(id = id, date = date)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filtredList by viewModel.filteredList.collectAsStateWithLifecycle()

    when (state) {
        TrainingFormUiState.Loading -> TrainingPagePlaceholder("", onBackClick)

        is TrainingFormUiState.TrainingForm -> {
            TrainingFormView(
                state = state as TrainingFormUiState.TrainingForm,
                onEvent = viewModel::onEvent,
                filtered = filtredList,
                onBackClick = onBackClick
            )
        }

        is TrainingFormUiState.Error -> {

        }

        TrainingFormUiState.Success -> {
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }
    }
}


@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingFormView(
    state: TrainingFormUiState.TrainingForm,
    onEvent: (TrainingFormEvent) -> Unit,
    onBackClick: () -> Unit,
    filtered: List<ExerciseWithMuscles>,
) {
    val context = LocalContext.current

    val stateBottomSheet = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val reorderState = rememberReorderState(
        listState = listState,
        items = state.item.items,
        scope = scope,
        idProvided = { it.id }
    ) { from, to ->
        onEvent(TrainingFormEvent.Undoable(TrainingUndoableAction.MoveExercise(from, to)))
    }

    val modesFlattened = remember(state.items, state.modes) {
        state.modes.flatMap { mode ->
            mode.categories<Any, Any>(state.items, context).map { filter ->
                filter to mode
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = context.getString(message),
                duration = SnackbarDuration.Short
            )
            onEvent(TrainingFormEvent.OnErrorShown)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
    ) {
        ScreenLazyColumn(
            list = listState,
            arrangement = Arrangement.spacedBy(8.dp),
            navigationType = TypeTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = if (state.item.title.isNotEmpty()) stringResource(
                    R.string.training_number,
                    state.item.title
                )
                else stringResource(R.string.training_title),
                painter = R.drawable.nav_save,
                onClick = { onEvent(TrainingFormEvent.Save) }
            )
        ) {
            item {
                DateCard(
                    onDateClick = { onEvent(TrainingFormEvent.CalendarState(true)) },
                    date = state.item.date
                )
                DropdownCheckboxMenu(
                    items = state.categories,
                    title = stringResource(com.sinya.projects.pagetraining.R.string.routine),
                    onInfoClick = {
                        onEvent(
                            TrainingFormEvent.OpenDialogGuide(
                                context.getString(com.sinya.projects.pagetraining.R.string.routine),
                                context.getString(com.sinya.projects.pagetraining.R.string.routine_description)
                            )
                        )
                    },
                    alignment = Alignment.Start,
                    selectedItems = listOf(state.item.category),
                    expanded = expanded,
                    onExpanded = { expanded = it },
                    nameObject = { list ->
                        list.joinToString {
                            it?.name ?: context.getString(R.string.not_category)
                        }
                    },
                    onPlusClick = {
                        scope.launch {
                            onEvent(TrainingFormEvent.OpenBottomSheetCategory)
                            stateBottomSheet.value = !stateBottomSheet.value
                        }
                    }
                ) { item ->
                    DropdownElement(
                        title = item?.name ?: stringResource(R.string.not_category),
                        onClick = {
                            onEvent(TrainingFormEvent.OnSelectedCategory(item))
                            expanded = false
                        }
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            items(
                items = state.item.items,
                key = { it.id }
            ) { item ->
                val isDragging = reorderState.draggedItemId == item.id
                var isMenuExpanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    SwipeTrainingCard(
                        modifier = Modifier
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .alpha(if (isDragging) 0f else 1f)
                            .pointerInput(item.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { reorderState.onDragStart(item.id) },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        reorderState.onDrag(dragAmount.y)
                                    },
                                    onDragEnd = { reorderState.onDragEnd() },
                                    onDragCancel = { reorderState.onDragEnd() }
                                )
                            },
                        item = item,
                        onMenuClick = { isMenuExpanded = true },
                        onPlusClick = { id ->
                            onEvent(
                                TrainingFormEvent.Undoable(
                                    TrainingUndoableAction.AddSet(id)
                                )
                            )
                        },
                        onMinusClick = {
                            onEvent(
                                TrainingFormEvent.Undoable(
                                    TrainingUndoableAction.DeleteExercise(
                                        item,
                                        state.item.items.indexOfFirst { it == item })
                                )
                            )
                        },
                        onEditSet = { id, index1, value, valState ->
                            onEvent(
                                TrainingFormEvent.Undoable(
                                    TrainingUndoableAction.EditSet(
                                        id,
                                        index1,
                                        when (valState) {
                                            true -> item.items[index1].count
                                            else -> item.items[index1].weight
                                        },
                                        value,
                                        valState
                                    )
                                )
                            )
                        },
                        onDeleteSet = { id, index1 ->
                            onEvent(
                                TrainingFormEvent.Undoable(
                                    TrainingUndoableAction.DeleteSet(
                                        id,
                                        index1,
                                        item.items[index1]
                                    )
                                )
                            )
                        }
                    )

                    PopupMenu(
                        isMenuExpanded = isMenuExpanded,
                        onMenuExpanded = { isMenuExpanded = it },
                        item = item,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        listItems = listOf(
                            PopupItem(
                                title = stringResource(com.sinya.projects.pagetraining.R.string.weight_of_body),
                                onClick = {
                                    onEvent(
                                        TrainingFormEvent.Undoable(
                                            TrainingUndoableAction.ToggleWeight(
                                                item.id,
                                                item.weightState,
                                                item.weightState
                                            )
                                        )
                                    )
                                },
                                icon = when (item.weightState) {
                                    WeightState.MATTER -> R.drawable.train_muscul
                                    WeightState.NOT_MATTER -> R.drawable.train_weight_no
                                },
                            ),
                            PopupItem(
                                title = stringResource(com.sinya.projects.pagetraining.R.string.exercise_on_count),
                                onClick = {
                                    onEvent(
                                        TrainingFormEvent.Undoable(
                                            TrainingUndoableAction.ToggleMode(
                                                item.id,
                                                item.mode,
                                                item.mode
                                            )
                                        )
                                    )
                                },
                                icon = when (item.mode) {
                                    TrainingMode.COUNT -> R.drawable.train_count
                                    TrainingMode.TIME -> R.drawable.train_time
                                },
                            ),
                            PopupItem(
                                title = stringResource(com.sinya.projects.pagetraining.R.string.hide_of_summary),
                                onClick = {
                                    onEvent(
                                        TrainingFormEvent.Undoable(
                                            TrainingUndoableAction.VisibleExercise(
                                                item.id,
                                                item.state,
                                                Visibility.getState(!item.state.state)
                                            )
                                        )
                                    )
                                },
                                icon = when (item.state) {
                                    Visibility.VISIBLE -> R.drawable.ic_eye
                                    Visibility.INVISIBLE -> R.drawable.ic_eye_no
                                }
                            ),
                            PopupItem(
                                title = stringResource(com.sinya.projects.pagetraining.R.string.add_set),
                                onClick = {
                                    onEvent(
                                        TrainingFormEvent.Undoable(
                                            TrainingUndoableAction.AddSet(item.id)
                                        )
                                    )
                                },
                                icon = R.drawable.ic_plus
                            ),
                            PopupItem(
                                title = stringResource(R.string.description),
                                onClick = {
                                    onEvent(TrainingFormEvent.OpenDialog(item.id))
                                },
                                icon = R.drawable.ic_info
                            ),
                            PopupItem(
                                title = stringResource(com.sinya.projects.pagetraining.R.string.delete_exercise),
                                onClick = {
                                    onEvent(
                                        TrainingFormEvent.Undoable(
                                            TrainingUndoableAction.DeleteExercise(
                                                item,
                                                state.item.items.indexOfFirst { it == item })
                                        )
                                    )
                                },
                                icon = R.drawable.morn_trash
                            )
                        )
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CustomButton(
                        onClick = {
                            scope.launch {
                                onEvent(TrainingFormEvent.OpenBottomSheetTraining)
                                stateBottomSheet.value = !stateBottomSheet.value
                            }
                        },
                        text = stringResource(R.string.add_exercise),
                    )
                }
                Spacer(Modifier.height(180.dp))
            }
        }


        DragOverlay(
            reorderState = reorderState,
            index = state.item.items.indexOfFirst { it.id == reorderState.draggedItemId },
            listState = listState,
        ) {
            SwipeTrainingCardContent(
                modifier = Modifier.padding(horizontal = 8.dp),
                expanded = false,
                onExpanded = { },
                item = state.item.items.find { it.id == reorderState.draggedItemId }
                    ?: state.item.items.first(),
                onPlusClick = {},
                onEditSet = { _, _, _, _ -> },
                onDeleteSet = { _, _ -> },
                onMenuClick = {}
            )
        }

        state.dialogContent?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(TrainingFormEvent.OpenDialog(null))
                },
                content = {
                    GuideDescriptionView(
                        title = it.name,
                        description = it.description,
                        image = null
                    )
                }
            )
        }

        if (state.calendarVisible) {
            DatePickerModal(
                onDateSelected = { date ->
                    onEvent(
                        TrainingFormEvent.Undoable(
                            TrainingUndoableAction.OnPickDate(
                                parseMillisToDateTime(date),
                                state.item.date
                            )
                        )
                    )
                },
                onDismiss = { onEvent(TrainingFormEvent.CalendarState(false)) }
            )
        }

        UndoBottomNavigation(
            modifier = Modifier
                .padding(bottom = 40.dp, start = 16.dp, end = 16.dp),
            onUndoClick = { onEvent(TrainingFormEvent.Undo) },
            canUndo = state.canUndo,
            isUndoing = state.isUndoing,
            onRedoClick = { onEvent(TrainingFormEvent.Redo) },
            canRedo = state.canRedo,
        )

        CustomSnackbar(snackbarHostState)

        if ((state.bottomSheetCategoryStatus != null || state.bottomSheetTrainingQuery != null) && stateBottomSheet.value) {
            CustomModalSheet(
                modifier = Modifier,
                onDismiss = { stateBottomSheet.value = !stateBottomSheet.value }
            ) {
                if (state.bottomSheetCategoryStatus != null) {
                    CategorySheetContent(
                        category = state.bottomSheetCategoryStatus.categoryName,
                        query = state.bottomSheetCategoryStatus.query,
                        isError = state.bottomSheetCategoryStatus.isError,
                        filtered = filtered,
                        onModeClick = { mode, value ->
                            onEvent(
                                TrainingFormEvent.SortParamChange(
                                    mode,
                                    value
                                )
                            )
                        },
                        onClickSuccess = { onEvent(TrainingFormEvent.OnCreateCategory) },
                        onCategoryChange = { s -> onEvent(TrainingFormEvent.OnNameChange(s)) },
                        onQueryChange = { s -> onEvent(TrainingFormEvent.OnQueryChange(s)) },
                        onToggle = { id -> onEvent(TrainingFormEvent.Toggle(id)) },
                        onCancelClick = { stateBottomSheet.value = !stateBottomSheet.value },
                        modesFlattened = modesFlattened
                    )
                } else if (state.bottomSheetTrainingQuery != null) {
                    ExerciseSheetContent(
                        query = state.bottomSheetTrainingQuery,
                        filtered = filtered,
                        onQueryChange = { s -> onEvent(TrainingFormEvent.OnQueryChange(s)) },
                        onModeClick = { mode, value ->
                            onEvent(
                                TrainingFormEvent.SortParamChange(
                                    mode,
                                    value
                                )
                            )
                        },
                        onToggle = { id -> onEvent(TrainingFormEvent.Toggle(id)) },
                        onClickSuccess = { onEvent(TrainingFormEvent.AddExercise) },
                        onCancelClick = { stateBottomSheet.value = !stateBottomSheet.value },
                        modesFlattened = modesFlattened
                    )
                }
            }
        }
    }
}

