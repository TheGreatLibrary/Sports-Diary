package com.sinya.projects.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.model.PopupItem
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.exercises.components.ExercisesPlaceholder
import com.sinya.projects.ui.R.drawable
import com.sinya.projects.ui.R.string
import com.sinya.projects.ui.components.PopupMenu
import com.sinya.projects.ui.components.screen.ScreenLazySmartHeader
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.DeleteSheetContent
import com.sinya.projects.ui.components.sheet.components.SortedExercisesSheetContent
import com.sinya.projects.ui.components.smartHeader.util.rememberSmartHeaderManager
import com.sinya.projects.ui.components.CustomSnackbar

@Composable
fun ExercisesScreen(
    onBackClick: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    onEditClick: (Int?) -> Unit
) {
    val viewModel: ExercisesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ExercisesUiState.Loading -> ExercisesPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(string.sports_exercises_title),
        )

        is ExercisesUiState.Success -> ExercisesScreenView(
            state = state as ExercisesUiState.Success,
            exercises = viewModel.filtered(),
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onExerciseClick = onExerciseClick,
            onEditClick = onEditClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExercisesScreenView(
    state: ExercisesUiState.Success,
    exercises: List<ExerciseWithMuscles>,
    onEvent: (ExercisesEvent) -> Unit,
    onBackClick: () -> Unit,
    onEditClick: (Int?) -> Unit,
    onExerciseClick: (Int) -> Unit,
) {
    val density = LocalDensity.current

    val pullToRefreshState = rememberPullToRefreshState()
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()

    val snackbarHostState = remember { SnackbarHostState() }
    var sortState by remember { mutableStateOf(false) }
    var deleteState by remember { mutableStateOf(false) }

    val headerVisibleHeightDp = with(density) {
        (smartHeader.headerHeightPx + smartHeader.headerOffsetPx)
            .coerceAtLeast(0f)
            .toDp()
    }

    val context = LocalContext.current
    val modesFlattened = remember(exercises, state.selectedModes) {
        state.selectedModes.flatMap { mode ->
            mode.categories<Any, Any>(exercises, context).map { filter ->
                filter to mode
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ExercisesEvent.OnErrorShown)
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(ExercisesEvent.ReloadData) },
        state = pullToRefreshState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .nestedScroll(smartHeader.headerScrollConnection),
        contentAlignment = Alignment.TopCenter
    ) {
        ScreenLazySmartHeader(
            modifier = Modifier.padding(top = headerVisibleHeightDp),
            list = smartHeader.listState,
            query = state.query,
            onQueryChange = { s -> onEvent(ExercisesEvent.OnQueryChange(s)) },
            arrangement = Arrangement.spacedBy(8.dp),
            manager = manager,
            navigationType = TypeTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = stringResource(string.sports_exercises_title),
                painter = drawable.ic_plus,
                onClick = { onEditClick(null) }
            ),
            onOptionSelected = { sortState = true }
        ) {
            item {
                if (exercises.isEmpty()) {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(string.not_found_data),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            items(
                items = exercises,
                key = { it.id }
            ) { item ->
                var isMenuExpanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    com.sinya.projects.ui.components.swipeCard.SwipeCard(
                        modifier = Modifier,
                        title = item.name,
                        description = item.muscles.joinToString(", "),
                        swipeToDelete = {
                            onEvent(ExercisesEvent.SelectTraining(item.id))
                            deleteState = true
                        },
                        onClick = { onExerciseClick(item.id) },
                        swipeToChange = null,
                        selected = false,
                        icon = null,
                        selectionState = false,
                        onMenuClick = { isMenuExpanded = true },
                        onSelected = { onEvent(ExercisesEvent.SelectTraining(item.id)) },
                        startIconBack = null,
                        endIconBack = drawable.morn_trash,
                    )

                    PopupMenu(
                        isMenuExpanded = isMenuExpanded,
                        onMenuExpanded = { isMenuExpanded = it },
                        item = item,
                        listItems =  buildList {
                            add(
                                PopupItem(
                                    title = stringResource(string.open),
                                    onClick = { onExerciseClick(item.id) },
                                    icon = drawable.back_import
                                )
                            )

                            if (item.isCustom) {
                                add(
                                    PopupItem(
                                        icon = drawable.icon_edit,
                                        title = stringResource(string.edit),
                                        onClick = { onEditClick(item.id) }
                                    )
                                )
                            }

                            add(
                                PopupItem(
                                    title = stringResource(string.delete),
                                    onClick = {
                                        onEvent(ExercisesEvent.SelectTraining(item.id))
                                        deleteState = true
                                    },
                                    icon = drawable.morn_trash
                                )
                            )
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }

        CustomSnackbar(snackbarHostState)

        if (sortState || (state.deleteItems.isNotEmpty() && deleteState)) {
            CustomModalSheet(
                onDismiss = {
                    when {
                        sortState -> sortState = false
                        deleteState -> deleteState = false
                    }
                },
                content = {
                    when {
                        sortState -> {
                            SortedExercisesSheetContent(
                                onOptionSelected = { mode, value ->
                                    onEvent(ExercisesEvent.SortParamChange(mode, value))
                                },
                                modesFlattened = modesFlattened
                            )
                        }

                        deleteState -> {
                            DeleteSheetContent(
                                onSuccess = {
                                    onEvent(ExercisesEvent.DeleteExercises)
                                    deleteState = false
                                },
                            )
                        }
                    }
                }
            )
        }

    }
}