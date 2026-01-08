package com.sinya.projects.sportsdiary.presentation.proportions

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.filterByYearMonth
import com.sinya.projects.sportsdiary.data.database.entity.localDateOrNull
import com.sinya.projects.sportsdiary.data.database.entity.monthsForYear
import com.sinya.projects.sportsdiary.data.database.entity.years
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.RadioItem
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainings.dateFmt
import com.sinya.projects.sportsdiary.ui.features.SortedRow
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog

@Composable
fun ProportionsScreen(
    viewModel: ProportionsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ProportionsUiState.Loading -> PlaceholderScreen()

        is ProportionsUiState.Success -> ProportionsScreenView(
            state = state as ProportionsUiState.Success,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onProportionClick = onProportionClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProportionsScreenView(
    state: ProportionsUiState.Success,
    onBackClick: () -> Unit,
    onEvent: (ProportionsEvent) -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    val all = stringResource(R.string.all)
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage, state.isRefreshing) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ProportionsEvent.OnErrorShown)
        }
        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
        LaunchedEffect(Unit) {
            onEvent(ProportionsEvent.ReloadData)
        }
    }

    val years = remember(state.proportions, state.selectedMode) {
        listOf(RadioItem(all, null, -1)) +
                state.proportions.years.map { RadioItem(it.toString(), null, it) }
    }
    val months = remember(state.proportions, state.selectedMode) {
        listOf(RadioItem(all, null, -1)) +
                state.proportions.monthsForYear(state.selectedMode.year).map { RadioItem(it.toString(), null, it) }
    }
    val grouped = remember(state.proportions, state.selectedMode) {
        state.proportions.filterByYearMonth(state.selectedMode.year, state.selectedMode.month)
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
                        title = stringResource(R.string.proportions_title),
                        painter = R.drawable.ic_plus,
                        onClick = { onProportionClick(null) }
                    )
                )
                Spacer(Modifier.height(20.dp))
            }

            item {
                SortedRow(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    title = stringResource(R.string.year),
                    radioOptions = years,
                    selectedOption = state.selectedMode.year,
                    onOptionSelected = { year ->
                        onEvent(ProportionsEvent.YearChange(year))
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                )
                Spacer(Modifier.height(15.dp))
                SortedRow(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    title = stringResource(R.string.month),
                    radioOptions = months,
                    selectedOption = state.selectedMode.month,
                    onOptionSelected = { month ->
                        onEvent(ProportionsEvent.MonthChange(month))
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                )
                Spacer(Modifier.height(20.dp))
            }

            items(
                items = grouped,
                key = { it.id }
            ) {
                val dateText = it.localDateOrNull()?.format(dateFmt) ?: it.date

                SwipeCard(
                    modifier = Modifier.padding(bottom = 8.dp),
                    id = it.id,
                    title = stringResource(R.string.proportion_number, it.id),
                    description = dateText,
                    onTrainingClick = { onProportionClick(it.id) },
                    onDelete = { id -> onEvent(ProportionsEvent.OpenDialog(id)) },
                )
            }
        }

        state.deleteDialogId?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(ProportionsEvent.OpenDialog(null))
                },
                content = {
                    DeleteDialogView(
                        onSuccess = { onEvent(ProportionsEvent.DeleteProportion) },
                        onBack = { onEvent(ProportionsEvent.OpenDialog(null)) }
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