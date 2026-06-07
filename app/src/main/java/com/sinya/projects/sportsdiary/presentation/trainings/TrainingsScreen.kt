package com.sinya.projects.sportsdiary.presentation.trainings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.core.domain.model.localDateOrNull
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainings.components.TrainingsHeader
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.smartHeader.rememberSmartHeaderManager
import com.sinya.projects.sportsdiary.ui.features.smartHeader.smartHeader
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
    val density = LocalDensity.current
    val pullToRefreshState = rememberPullToRefreshState()
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()
    val snackbarHostState = remember { SnackbarHostState() }

    val headerVisibleHeightDp = with(density) {
        (smartHeader.headerHeightPx + smartHeader.headerOffsetPx)
            .coerceAtLeast(0f)
            .toDp()
    }

    val grouped = remember(state.trainings, state.selectedMode) {
        state.selectedMode.filter(state.trainings)
    }

    val categories = remember(state.trainings, state.selectedMode) {
        state.selectedMode.categories<Any, Any>(state.trainings, context)
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(TrainingEvent.OnErrorShown)
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(TrainingEvent.ReloadData) },
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
            items(
                items = grouped,
                key = { it.id }
            ) {
                val dateText = it.localDateOrNull()?.format(dateFmt) ?: it.date

                SwipeCard(
                    modifier = Modifier,
                    id = it.id,
                    title = stringResource(R.string.training_number, it.name),
                    description = "${it.category ?: stringResource(R.string.not_category)} • $dateText",
                    onTrainingClick = { onTrainingClick(it.id) },
                    onDelete = { id -> onEvent(TrainingEvent.OpenDialog(id)) },
                )
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }

        TrainingsHeader(
            modifier = Modifier.smartHeader(manager),
            onOptionSelected = { value ->
                onEvent(TrainingEvent.SortParamChange(value))
            },
            onModeChange = {
                onEvent(TrainingEvent.ModeChange(it))
            },
            selectedMode = state.selectedMode,
            categories = categories,
            navigationType = TypeAppTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.trainings_title),
                painter = R.drawable.ic_plus,
                onClick = { onTrainingClick(null) }
            )
        )

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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}