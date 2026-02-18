package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.categoryPage.components.DragOverlay
import com.sinya.projects.sportsdiary.presentation.categoryPage.components.ExerciseSheetContent
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CategorySheetContent
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomDropdownMenu
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.SwipeTrainingCard
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.SwipeTrainingCardContent
import com.sinya.projects.sportsdiary.presentation.trainings.TrainingEvent
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.DateCard
import com.sinya.projects.sportsdiary.ui.features.DatePickerModal
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.rememberReorderState
import kotlinx.coroutines.launch

@Composable
fun TrainingPageScreen(
    id: Int?,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: TrainingPageViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        TrainingPageUiState.Loading -> PlaceholderScreen()

        is TrainingPageUiState.TrainingForm -> TrainingPage(
            state = state as TrainingPageUiState.TrainingForm,
            onEvent = viewModel::onEvent,
            filtered = viewModel.filtered(),
            onBackClick = onBackClick
        )

        is TrainingPageUiState.Error -> ErrorScreen((state as TrainingPageUiState.Error).errorMessage)

        TrainingPageUiState.Success -> {
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingPage(
    state: TrainingPageUiState.TrainingForm,
    onEvent: (TrainingPageEvent) -> Unit,
    onBackClick: () -> Unit,
    filtered: List<ExerciseWithMuscles>
) {
    val title = stringResource(R.string.constructor)
    val description = stringResource(R.string.category_description)

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
//    val scaffoldState = rememberBottomSheetScaffoldState()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )

    val scope = rememberCoroutineScope()
    val reorderState = rememberReorderState(
        listState = listState,
        items = state.item.items,
        scope = scope,
        idProvided = { it.id }
    ) { from, to -> onEvent(TrainingPageEvent.MoveExercise(from, to)) }

    val context = LocalContext.current
    val modesFlattened = remember(state.items, state.modes) {
        state.modes.flatMap { mode ->
            mode.categories<Any, Any>(state.items, context).map { filter ->
                filter to mode
            }
        }
    }

    LaunchedEffect(state.bottomSheetCategoryStatus) {
        if (state.bottomSheetCategoryStatus == null) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(TrainingPageEvent.OnErrorShown)
        }
    }

    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = {
            if (state.bottomSheetCategoryStatus != null) {
                CategorySheetContent(
                    category = state.bottomSheetCategoryStatus.categoryName,
                    query = state.bottomSheetCategoryStatus.query,
                    isError = state.bottomSheetCategoryStatus.isError,
                    filtered = filtered,
                    onModeClick = { mode, value ->
                        onEvent(
                            TrainingPageEvent.SortParamChange(
                                mode,
                                value
                            )
                        )
                    },
                    onCategoryChange = { s -> onEvent(TrainingPageEvent.OnNameChange(s)) },
                    onQueryChange = { s -> onEvent(TrainingPageEvent.OnQueryChange(s)) },
                    onToggle = { id -> onEvent(TrainingPageEvent.Toggle(id)) },
                    onClickSuccess = { onEvent(TrainingPageEvent.OnCreateCategory) },
                    scaffoldState = scaffoldState,
                    modesFlattened = modesFlattened
                )
            } else if (state.bottomSheetTrainingQuery != null) {
                ExerciseSheetContent(
                    query = state.bottomSheetTrainingQuery,
                    filtered = filtered,
                    onQueryChange = { s -> onEvent(TrainingPageEvent.OnQueryChange(s)) },
                    onModeClick = { mode, value ->
                        onEvent(
                            TrainingPageEvent.SortParamChange(
                                mode,
                                value
                            )
                        )
                    },
                    onToggle = { id -> onEvent(TrainingPageEvent.Toggle(id)) },
                    onClickSuccess = { onEvent(TrainingPageEvent.AddExercise) },
                    scaffoldState = scaffoldState,
                    modesFlattened = modesFlattened
                )
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(Modifier.height(40.dp))
                    NavigationTopBar(
                        type = TypeAppTopNavigation.WithIcon(
                            onBackClick = onBackClick,
                            title = if (state.item.title.isNotEmpty()) stringResource(
                                R.string.training_number,
                                state.item.title
                            )
                            else stringResource(R.string.training_title),
                            painter = R.drawable.nav_save,
                            onClick = { onEvent(TrainingPageEvent.Save) }
                        )
                    )
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    DateCard(
                        onDateClick = { onEvent(TrainingPageEvent.CalendarState(true)) },
                        date = state.item.date
                    )
                    Spacer(Modifier.height(8.dp))
                    CustomDropdownMenu(
                        items = state.categories,
                        title = title,
                        onInfoClick = {
                            onEvent(TrainingPageEvent.OpenDialogGuide(title, description))
                        },
                        selectedItem = state.item.category,
                        onOpenMenu = { /*onEvent(TrainingPageEvent.UpdateCategories)*/ },
                        onSelectedCategory = { name ->
                            onEvent(
                                TrainingPageEvent.OnSelectedCategory(
                                    name
                                )
                            )
                        },
                        nameObject = { it.name },
                        onPlusClick = {
                            scope.launch {
                                onEvent(TrainingPageEvent.OpenBottomSheetCategory)
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                }

                items(
                    items = state.item.items,
                    key = { it.id }
                ) { item ->
                    val isDragging = reorderState.draggedItemId == item.id

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
                        onInfoClick = { id -> onEvent(TrainingPageEvent.OpenDialog(id)) },
                        onPlusClick = { id -> onEvent(TrainingPageEvent.AddSet(id)) },
                        onMinusClick = { id -> onEvent(TrainingPageEvent.OpenDialogOnDelete(id)) },
                        onEditSet = { id, index1, value, valState ->
                            onEvent(
                                TrainingPageEvent.EditSet(
                                    id,
                                    index1,
                                    value,
                                    valState
                                )
                            )
                        },
                        onDeleteSet = { id, index1 ->
                            onEvent(TrainingPageEvent.DeleteSet(id, index1))
                        }
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
                                    onEvent(TrainingPageEvent.OpenBottomSheetTraining)
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
                listState = listState,
            ) {
                SwipeTrainingCardContent(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    expanded = false,
                    onExpanded = { },
                    item = state.item.items.find { it.id == reorderState.draggedItemId }
                        ?: state.item.items.first(),
                    onInfoClick = {},
                    onPlusClick = {},
                    onEditSet = { _, _, _, _ -> },
                    onDeleteSet = { _, _ -> }
                )
            }

            state.dialogContent?.let {
                GuideDialog(
                    onDismiss = {
                        onEvent(TrainingPageEvent.OpenDialog(null))
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


            state.deleteDialogId?.let {
                GuideDialog(
                    onDismiss = { onEvent(TrainingPageEvent.OpenDialogOnDelete(null)) },
                    content = {
                        DeleteDialogView(
                            onSuccess = { onEvent(TrainingPageEvent.Delete) },
                            checked = !state.trainingWarningState,
                            checkBoxToggle = { onEvent(TrainingPageEvent.CheckBoxToggle(it)) },
                            onBack = { onEvent(TrainingPageEvent.OpenDialogOnDelete(null)) }
                        )
                    }
                )
            }


            if (state.calendarVisible) {
                DatePickerModal(
                    onDateSelected = { date -> onEvent(TrainingPageEvent.OnPickDate(date)) },
                    onDismiss = { onEvent(TrainingPageEvent.CalendarState(false)) }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }
}


