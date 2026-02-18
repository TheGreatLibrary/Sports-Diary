package com.sinya.projects.sportsdiary.presentation.exercises

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.domain.model.RadioItem
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.exercises.components.ExercisesSheetContent
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.SearchContainer
import com.sinya.projects.sportsdiary.ui.features.SortedRow
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import kotlinx.coroutines.launch

@Composable
fun ExercisesScreen(
    viewModel: ExercisesViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    onEditClick: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ExercisesUiState.Loading -> PlaceholderScreen()

        is ExercisesUiState.Success -> ExercisesScreenView(
            state = state as ExercisesUiState.Success,
            exercises = viewModel.filtered(),
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onExerciseClick = onExerciseClick,
            onEditClick = onEditClick
        )

        is ExercisesUiState.Error -> ErrorScreen((state as ExercisesUiState.Error).message)
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
    val scope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage, state.isRefreshing) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ExercisesEvent.OnErrorShown)
        }
        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
        LaunchedEffect(Unit) {
            onEvent(ExercisesEvent.ReloadData)
        }
    }

    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = {
            ExercisesSheetContent(
                scaffoldState = scaffoldState,
                list = state.selectedModes,
                exercises = state.exercises,
                onEvent = onEvent
            )
        }
    ) {
        Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 50.dp),
            ) {
                item {
                    NavigationTopBar(
                        type = TypeAppTopNavigation.WithIcon(
                            onBackClick = onBackClick,
                            title = stringResource(R.string.sport_exercises_title),
                            painter = R.drawable.ic_plus,
                            onClick = { onEditClick(null) }
                        )
                    )
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    SortedRow(
                        modifier = Modifier.padding(4.dp),
                        title = stringResource(R.string.sort_title),
                        radioOptions = listOf(RadioItem(null, R.drawable.icon_sort, 0)),
                        selectedOption = 0,
                        onOptionSelected = {
                            scope.launch { scaffoldState.bottomSheetState.expand() }
                        },
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                    Spacer(Modifier.height(15.dp))
                }

                item {
                    SearchContainer(
                        searchQuery = state.query,
                        onClear = { onEvent(ExercisesEvent.OnQueryChange("")) },
                        onValueChanged = { s -> onEvent(ExercisesEvent.OnQueryChange(s)) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    Spacer(Modifier.height(20.dp))

                    if (exercises.isEmpty()) {
                        Text(
                            text = stringResource(R.string.nothing_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                items(
                    items = exercises,
                    key = { it.id }
                ) { item ->
                    SwipeCard(
                        modifier = Modifier.padding(bottom = 8.dp),
                        id = item.id,
                        title = item.name,
                        maxLines = 1,
                        description = item.muscles.joinToString(", "),
                        onTrainingClick = { onExerciseClick(item.id) },
                        onDelete = { onEvent(ExercisesEvent.OpenDialog(item.id)) },
                    )
                }

                item {
                    Spacer(Modifier.height(80.dp))
                }
            }

            state.deleteDialogId?.let {
                GuideDialog(
                    onDismiss = { onEvent(ExercisesEvent.OpenDialog(null)) },
                    content = {
                        DeleteDialogView(
                            onSuccess = { onEvent(ExercisesEvent.Delete) },
                            onBack = { onEvent(ExercisesEvent.OpenDialog(null)) }
                        )
                    }
                )
            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
            )
            
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }
}