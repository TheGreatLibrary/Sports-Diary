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
import com.sinya.projects.sportsdiary.domain.enums.SortMode
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.domain.model.RadioItem
import com.sinya.projects.sportsdiary.domain.model.categories
import com.sinya.projects.sportsdiary.domain.model.filterByMuscle
import com.sinya.projects.sportsdiary.domain.model.filterByYearMonth
import com.sinya.projects.sportsdiary.domain.model.localDateOrNull
import com.sinya.projects.sportsdiary.domain.model.monthsForYear
import com.sinya.projects.sportsdiary.domain.model.years
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.SortedRow
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.utils.getString
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
    val all = stringResource(R.string.all)
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
        when (state.selectedMode.mode) {
            SortMode.TIME -> {
                val selectedMode = state.selectedMode as ModeOfSorting.TimeMode
                state.trainings.filterByYearMonth(selectedMode.year, selectedMode.month)
            }

            SortMode.MUSCLE -> {
                val selectedMode = state.selectedMode as ModeOfSorting.MuscleMode
                state.trainings.filterByMuscle(selectedMode.category)
            }
        }
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
                    radioOptions = SortMode.entries.map {
                       it.radioItem.copy(
                            value = it.code
                        )
                    },
                    selectedOption = state.selectedMode.mode.code,
                    onOptionSelected = { code ->
                        val mode = SortMode.fromIndex(code)
                        onEvent(TrainingEvent.ModeChange(mode))
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                )
                Spacer(Modifier.height(15.dp))
            }

            item {
                when (state.selectedMode.mode) {
                    SortMode.TIME -> {
                        val years = remember(state.trainings, state.selectedMode) {
                            listOf(RadioItem(all, null, -1)) +
                                    state.trainings.years.map { RadioItem(it.toString(), null, it) }
                        }
                        val months = remember(state.trainings, state.selectedMode) {
                            listOf(RadioItem(all, null, -1)) +
                                    state.trainings.monthsForYear((state.selectedMode as ModeOfSorting.TimeMode).year).map { RadioItem(it.toString(), null, it) }
                        }

                        SortedRow(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            title = stringResource(R.string.year),
                            radioOptions = years,
                            selectedOption = (state.selectedMode as ModeOfSorting.TimeMode).year,
                            onOptionSelected = { year ->
                                onEvent(TrainingEvent.YearChange(year))
                            },
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        Spacer(Modifier.height(15.dp))
                        SortedRow(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            title = stringResource(R.string.month),
                            radioOptions = months,
                            selectedOption = state.selectedMode.month,
                            onOptionSelected = { month ->
                                onEvent(TrainingEvent.MonthChange(month))
                            },
                            shape = MaterialTheme.shapes.extraLarge
                            )
                        Spacer(Modifier.height(20.dp))
                    }

                    SortMode.MUSCLE -> {
                        val categories = remember(state.trainings, state.selectedMode) {
                            listOf(RadioItem(all, null, "")) +
                                    state.trainings.categories.map { RadioItem(context.getString(it), null, it) }
                        }

                        SortedRow(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            title = stringResource(R.string.categories),
                            radioOptions = categories,
                            selectedOption = (state.selectedMode as ModeOfSorting.MuscleMode).category,
                            onOptionSelected = { category ->
                                onEvent(TrainingEvent.CategoryChange(category))
                            },
                            shape = MaterialTheme.shapes.extraLarge
                            )
                        Spacer(Modifier.height(20.dp))

                    }
                }
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
                    description = "${context.getString(it.category)} • $dateText",
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
                .align(Alignment.BottomCenter)
        )
    }
}