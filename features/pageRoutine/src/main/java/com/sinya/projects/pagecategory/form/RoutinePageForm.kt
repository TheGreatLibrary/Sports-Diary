package com.sinya.projects.pagecategory.form

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.model.PopupItem
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.pagecategory.form.components.RoutinePagePlaceholder
import com.sinya.projects.ui.R
import com.sinya.projects.ui.R.string
import com.sinya.projects.ui.components.PopupMenu
import com.sinya.projects.ui.components.reorderList.rememberReorderState
import com.sinya.projects.ui.components.screen.ScreenLazyColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.ExerciseSheetContent
import com.sinya.projects.ui.components.swipeCard.SwipeCard
import com.sinya.projects.ui.components.swipeCard.components.SwipeContent
import com.sinya.projects.ui.components.textField.ErrorTextField
import com.sinya.projects.ui.components.trainingConstructor.UndoBottomNavigation
import com.sinya.projects.ui.components.CustomButton
import com.sinya.projects.ui.components.CustomSnackbar
import com.sinya.projects.ui.components.DragOverlay
import com.sinya.projects.ui.components.dialog.GuideDescriptionView
import com.sinya.projects.ui.components.dialog.GuideDialog
import kotlinx.coroutines.launch

@Composable
fun RoutinePageForm(
    id: Int?,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: RoutinePageViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filtered by viewModel.filteredList.collectAsStateWithLifecycle()

    when (state) {
        RoutinePageUiState.Loading -> RoutinePagePlaceholder(
            onBackClick = onBackClick,
            title = "",
        )

        is RoutinePageUiState.RoutineForm -> {
            CategoryPageView(
                state = state as RoutinePageUiState.RoutineForm,
                onEvent = viewModel::onEvent,
                filtered = filtered,
                onBackClick = onBackClick
            )
        }

        is RoutinePageUiState.Error -> {

        }

        RoutinePageUiState.Success -> {
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPageView(
    state: RoutinePageUiState.RoutineForm,
    filtered: List<ExerciseWithMuscles>,
    onEvent: (RoutinePageEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val stateBottomSheet = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val reorderState = rememberReorderState(
        listState = listState,
        items = state.item.items,
        scope = scope,
        idProvided = { it.id }
    ) { from, to ->
        onEvent(RoutinePageEvent.Undoable(RoutineUndoableAction.MoveExercise(from, to)))
    }
    val modesFlattened = remember(state.sheetData.items, state.modes) {
        state.modes.flatMap { mode ->
            mode.categories<Any, Any>(state.sheetData.items, context).map { filter ->
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
            onEvent(RoutinePageEvent.OnErrorShown)
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
                title = state.item.category.name,
                painter = R.drawable.nav_save,
                onClick = { onEvent(RoutinePageEvent.Save) }
            )
        ) {
            item {
                ErrorTextField(
                    modifier = Modifier
                        .heightIn(max = 40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    value = state.item.category.name,
                    onValueChange = { s ->
                        onEvent(
                            RoutinePageEvent.Undoable(
                                RoutineUndoableAction.OnValueChange(
                                    state.item.category.name,
                                    s
                                )
                            )
                        )
                    },
                    placeholder = stringResource(R.string.category_name),
                    errorMessage = stringResource(string.category_name_is_empty),
                    maxLines = 1,
                    isError = state.isError,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }

            items(
                items = state.item.items,
                key = { "${it.id}_${it.mode}" }
            ) { item ->
                val isDragging = reorderState.draggedItemId == item.id
                var isMenuExpanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    SwipeCard(
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
                        title = item.title,
                        description = stringResource(
                            when (TrainingMode.getMode(item.mode)) {
                                TrainingMode.COUNT -> string.count_exercise
                                TrainingMode.TIME -> string.time_exercise
                            }
                        ),
                        swipeToDelete = {
                            onEvent(
                                RoutinePageEvent.Undoable(
                                    RoutineUndoableAction.DeleteExercise(
                                        item,
                                        state.item.items.indexOfFirst { it == item })
                                )
                            )
                        },
                        onClick = {
                            onEvent(RoutinePageEvent.OpenDialog(item.id))
                        },
                        swipeToChange = {
                            onEvent(
                                RoutinePageEvent.Undoable(
                                    RoutineUndoableAction.ToggleMode(
                                        item.id,
                                        item.mode
                                    )
                                )
                            )
                        },
                        selected = false,
                        icon = null,
                        selectionState = false,
                        onMenuClick = { isMenuExpanded = true },
                        onSelected = { },
                        startIconBack = R.drawable.cat_change,
                        endIconBack = R.drawable.morn_trash,
                    )

                    PopupMenu(
                        isMenuExpanded = isMenuExpanded,
                        onMenuExpanded = { isMenuExpanded = it },
                        item = item,
                        listItems = listOf(
                            PopupItem(
                                title = stringResource(string.open),
                                onClick = { onEvent(RoutinePageEvent.OpenDialog(item.id)) },
                                icon = R.drawable.back_import
                            ),
                            PopupItem(
                                title = stringResource(
                                    when (TrainingMode.getMode(item.mode)) {
                                        TrainingMode.COUNT -> string.count_exercise
                                        TrainingMode.TIME -> string.time_exercise
                                    }
                                ),
                                onClick = {
                                    onEvent(
                                        RoutinePageEvent.Undoable(
                                            RoutineUndoableAction.ToggleMode(
                                                item.id,
                                                item.mode
                                            )
                                        )
                                    )
                                },
                                icon = R.drawable.cat_change
                            ),
                            PopupItem(
                                title = stringResource(string.delete),
                                onClick = {
                                    onEvent(
                                        RoutinePageEvent.Undoable(
                                            RoutineUndoableAction.DeleteExercise(
                                                item,
                                                state.item.items.indexOfFirst { it == item })
                                        )
                                    )
                                },
                                icon = R.drawable.morn_trash
                            ),
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
                                stateBottomSheet.value = !stateBottomSheet.value
                            }
                        },
                        text = stringResource(string.add_exercise),
                    )
                }
                Spacer(Modifier.height(80.dp))
            }
        }

        DragOverlay(
            reorderState = reorderState,
            index = state.item.items.indexOfFirst { it.id == reorderState.draggedItemId },
            listState = listState
        ) {
            SwipeContent(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = state.item.items.find { it.id == reorderState.draggedItemId }?.title
                    ?: "",
                description = "",
                selected = false,
                onSelected = {  },
                onClick1 = {  },
                selectionState = false,
                icon = null,
                onMenuClick = {},
            )
        }

        state.dialogContent?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(RoutinePageEvent.OpenDialog(null))
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

        UndoBottomNavigation(
            modifier = Modifier
                .padding(bottom = 40.dp, start = 16.dp, end = 16.dp),
            onUndoClick = { onEvent(RoutinePageEvent.Undo) },
            canUndo = state.canUndo,
            isUndoing = state.isUndoing,
            onRedoClick = { onEvent(RoutinePageEvent.Redo) },
            canRedo = state.canRedo,
        )

        CustomSnackbar(snackbarHostState)

        if (stateBottomSheet.value) {
            CustomModalSheet(
                modifier = Modifier,
                onDismiss = { stateBottomSheet.value = !stateBottomSheet.value }
            ) {
                ExerciseSheetContent(
                    query = state.sheetData.query,
                    filtered = filtered,
                    onModeClick = { mode, value ->
                        onEvent(
                            RoutinePageEvent.SortParamChange(
                                mode,
                                value
                            )
                        )
                    },
                    onQueryChange = { s -> onEvent(RoutinePageEvent.OnQueryChange(s)) },
                    onToggle = { id -> onEvent(RoutinePageEvent.Toggle(id)) },
                    onClickSuccess = {
                        onEvent(RoutinePageEvent.AddExercise)
                        stateBottomSheet.value = !stateBottomSheet.value
                    },
                    modesFlattened = modesFlattened,
                    onCancelClick = { stateBottomSheet.value = !stateBottomSheet.value },
                )
            }
        }
    }
}