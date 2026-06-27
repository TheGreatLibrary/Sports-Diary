package com.sinya.projects.sportsdiary.presentation.categoryPage

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.enums.TrainingMode
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.presentation.categoryPage.components.CategoryPagePlaceholder
import com.sinya.projects.sportsdiary.ui.features.DragOverlay
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.ExerciseSheetContent
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomSnackbar
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.ScreenLazyColumn
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.SwipeCardContent
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.reorderList.rememberReorderState
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.UndoBottomNavigation
import kotlinx.coroutines.launch

@Composable
fun CategoryPageScreen(
    id: Int?,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: CategoryPageViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filtered by viewModel.filteredList.collectAsStateWithLifecycle()

    when (state) {
        CategoryPageUiState.Loading -> CategoryPagePlaceholder(
            onBackClick = onBackClick,
            title = "",
        )

        is CategoryPageUiState.CategoryForm -> {
            CategoryPageView(
                state = state as CategoryPageUiState.CategoryForm,
                onEvent = viewModel::onEvent,
                filtered = filtered,
                onBackClick = onBackClick
            )
        }

        is CategoryPageUiState.Error -> ErrorScreen(
            onBackClick,
            (state as CategoryPageUiState.Error).errorMessage
        )

        CategoryPageUiState.Success -> {
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
    state: CategoryPageUiState.CategoryForm,
    filtered: List<ExerciseWithMuscles>,
    onEvent: (CategoryPageEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val reorderState = rememberReorderState(
        listState = listState,
        items = state.item.items,
        scope = scope,
        idProvided = { it.id }
    ) { from, to ->
        onEvent(CategoryPageEvent.Undoable(CategoryUndoableAction.MoveExercise(from, to)))
    }
    val modesFlattened = remember(state.sheetData.items, state.sheetData.modes) {
        state.sheetData.modes.flatMap { mode ->
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
            onEvent(CategoryPageEvent.OnErrorShown)
        }
    }

    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = {
            ExerciseSheetContent(
                query = state.sheetData.query,
                filtered = filtered,
                onQueryChange = { s -> onEvent(CategoryPageEvent.OnQueryChange(s)) },
                onToggle = { id -> onEvent(CategoryPageEvent.Toggle(id)) },
                onClickSuccess = { onEvent(CategoryPageEvent.AddExercise) },
                scaffoldState = scaffoldState,
                modesFlattened = modesFlattened,
                onModeClick = { mode, value ->
                    onEvent(
                        CategoryPageEvent.SortParamChange(
                            mode,
                            value
                        )
                    )
                }
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenLazyColumn(
                list = listState,
                arrangement = Arrangement.spacedBy(8.dp),
                navigationType = TypeTopNavigation.WithIcon(
                    onBackClick = onBackClick,
                    title = state.item.category.name,
                    painter = R.drawable.nav_save,
                    onClick = { onEvent(CategoryPageEvent.Save) }
                )
            ) {
                item {
                    CustomTextField(
                        value = state.item.category.name,
                        onValueChange = { s ->
                            onEvent(
                                CategoryPageEvent.Undoable(
                                    CategoryUndoableAction.OnValueChange(
                                        state.item.category.name,
                                        s
                                    )
                                )
                            )
                        },
                        onTrailingClick = {
                            onEvent(
                                CategoryPageEvent.Undoable(
                                    CategoryUndoableAction.OnValueChange(
                                        state.item.category.name,
                                        ""
                                    )
                                )
                            )
                        },
                        placeholder = stringResource(R.string.category_name),
                        shape = MaterialTheme.shapes.extraLarge,
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        isError = state.isError,
                        imeAction = ImeAction.Done,
                        errorMessage = stringResource(R.string.category_name_is_empty)
                    )
                }

                items(
                    items = state.item.items,
                    key = { "${it.id}_${it.mode}" }
                ) { item ->
                    val isDragging = reorderState.draggedItemId == item.id

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
                        id = item.id,
                        title = item.title,
                        description = stringResource(
                            when (TrainingMode.getMode(item.mode)) {
                                TrainingMode.COUNT -> R.string.count_exercise
                                TrainingMode.TIME -> R.string.time_exercise
                            }
                        ),
                        painter1 = painterResource(R.drawable.cat_change),
                        onChange = {
                            onEvent(
                                CategoryPageEvent.Undoable(
                                    CategoryUndoableAction.ToggleMode(
                                        item.id,
                                        item.mode
                                    )
                                )
                            )
                        },
                        onTrainingClick = { onEvent(CategoryPageEvent.OpenDialog(item.id)) },
                        onDelete = {
                            onEvent(
                                CategoryPageEvent.Undoable(
                                    CategoryUndoableAction.DeleteExercise(
                                        item,
                                        state.item.items.indexOfFirst { it == item })
                                )
                            )
                        },
                    )
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomButton(
                            onClick = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            text = stringResource(R.string.add_exercise),
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
                SwipeCardContent(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = state.item.items.find { it.id == reorderState.draggedItemId }?.title
                        ?: "",
                    description = null,
                    onTrainingClick = { }
                )
            }

            state.dialogContent?.let {
                GuideDialog(
                    onDismiss = {
                        onEvent(CategoryPageEvent.OpenDialog(null))
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
                    .padding(bottom = 70.dp, start = 16.dp, end = 16.dp),
                onUndoClick = { onEvent(CategoryPageEvent.Undo) },
                canUndo = state.canUndo,
                isUndoing = state.isUndoing,
                onRedoClick = { onEvent(CategoryPageEvent.Redo) },
                canRedo = state.canRedo,
            )

            CustomSnackbar(snackbarHostState)
        }
    }
}