package com.sinya.projects.sportsdiary.presentation.exercises

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.exercises.components.ExercisesHeader
import com.sinya.projects.sportsdiary.presentation.exercises.components.SortedExercisesSheetContent
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.smartHeader.rememberSmartHeaderManager
import com.sinya.projects.sportsdiary.ui.features.smartHeader.smartHeader
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
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()
    val snackbarHostState = remember { SnackbarHostState() }

    val density = LocalDensity.current

    val headerVisibleHeightDp = with(density) {
        (smartHeader.headerHeightPx + smartHeader.headerOffsetPx)
            .coerceAtLeast(0f)
            .toDp()
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

    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = {
            SortedExercisesSheetContent(
                scaffoldState = scaffoldState,
                list = state.selectedModes,
                exercises = state.exercises,
                onEvent = onEvent
            )
        }
    ) {
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

            LazyColumn(
                modifier = Modifier.padding(top = headerVisibleHeightDp),
                state = smartHeader.listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    if (exercises.isEmpty()) {
                        Box(Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.nothing_found),
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
                    SwipeCard(
                        modifier = Modifier,
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

            ExercisesHeader(
                modifier = Modifier.smartHeader(manager),
                query = state.query,
                onQueryChange = { s -> onEvent(ExercisesEvent.OnQueryChange(s)) },
                onOptionSelected = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                navigationType = TypeAppTopNavigation.WithIcon(
                    onBackClick = onBackClick,
                    title = stringResource(R.string.sport_exercises_title),
                    painter = R.drawable.ic_plus,
                    onClick = { onEditClick(null) }
                )
            )

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

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }
}