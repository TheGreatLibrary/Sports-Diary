package com.sinya.projects.sportsdiary.presentation.trainings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.domain.model.localDateOrNull
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.SortedRow
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import java.time.format.DateTimeFormatter

val dateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
fun TrainingsScreen(
    viewModel: TrainingViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTrainingClick: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        TrainingUiState.Loading -> PlaceholderScreen()

        is TrainingUiState.Success -> TrainingsScreenView(
            state = state as TrainingUiState.Success,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onTrainingClick = onTrainingClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingsScreenView(
    state: TrainingUiState.Success,
    onEvent: (TrainingEvent) -> Unit,
    onBackClick: () -> Unit,
    onTrainingClick: (Int?) -> Unit,
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage, state.isRefreshing) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(TrainingEvent.OnErrorShown)
        }
        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
        LaunchedEffect(Unit) {
            onEvent(TrainingEvent.ReloadData)
        }
    }

    val grouped = remember(state.trainings, state.selectedMode) {
        state.selectedMode.filter(state.trainings)
    }

    val categories = remember(state.trainings, state.selectedMode) {
        state.selectedMode.categories<Any, Any>(state.trainings, context)
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 50.dp)
        ) {
            item {
                NavigationTopBar(
                    type = TypeAppTopNavigation.WithIcon(
                        onBackClick = onBackClick,
                        title = stringResource(R.string.trainings_title),
                        painter = R.drawable.ic_plus,
                        onClick = { onTrainingClick(null) }
                    )
                )
                Spacer(Modifier.height(20.dp))
            }

            item {
                SortedRow(
                    modifier = Modifier.padding(4.dp),
                    title = stringResource(R.string.sorted_by),
                    radioOptions = ModeOfSorting.trainingsModes(),
                    selectedOption = state.selectedMode.code,
                    onOptionSelected = { code ->
                        onEvent(TrainingEvent.ModeChange(code))
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                )
                Spacer(Modifier.height(15.dp))
            }

            items(categories) { filter ->
                SortedRow(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    title = stringResource(filter.titleRes),
                    radioOptions = filter.options,
                    selectedOption = filter.selectedValue,
                    onOptionSelected = { value ->
                        onEvent(TrainingEvent.SortParamChange(filter.onSelect(value)))
                    },
                    shape = filter.shape
                )
                Spacer(Modifier.height(15.dp))
            }

            items(
                items = grouped,
                key = { it.id }
            ) {
                val dateText = it.localDateOrNull()?.format(dateFmt) ?: it.date

                SwipeCard(
                    modifier = Modifier.padding(bottom = 8.dp),
                    id = it.id,
                    title = stringResource(R.string.training_number, it.name),
                    description = "${it.category?: stringResource(R.string.not_category)} • $dateText",
                    onTrainingClick = { onTrainingClick(it.id) },
                    onDelete = { id -> onEvent(TrainingEvent.OpenDialog(id)) },
                )
            }
        }

        state.deleteDialogId?.let {
            GuideDialog(
                onDismiss = { onEvent(TrainingEvent.OpenDialog(null)) },
                content = {
                    DeleteDialogView(
                        onSuccess = { onEvent(TrainingEvent.DeleteTraining) },
                        onBack = { onEvent(TrainingEvent.OpenDialog(null)) }
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