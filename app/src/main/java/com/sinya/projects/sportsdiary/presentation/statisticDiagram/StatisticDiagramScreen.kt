package com.sinya.projects.sportsdiary.presentation.statisticDiagram

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.CustomSnackbar
import com.sinya.projects.sportsdiary.ui.features.DropdownCheckboxMenu
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.chart.MultipleChart
import com.sinya.projects.sportsdiary.ui.features.chart.utils.ChartDataUtils

@Composable
fun StatisticDiagramScreen(
    onBackClick: () -> Unit,
    viewModel: StatisticDiagramViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        StatisticDiagramUiState.Loading -> PlaceholderScreen()

        is StatisticDiagramUiState.Success -> {
            StatisticDiagramView(
                onBackClick = onBackClick,
                state = state as StatisticDiagramUiState.Success,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
private fun StatisticDiagramView(
    onBackClick: () -> Unit,
    state: StatisticDiagramUiState.Success,
    onEvent: (StatisticDiagramEvent) -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = context.getString(message),
                duration = SnackbarDuration.Short
            )
            onEvent(StatisticDiagramEvent.OnMessage(null))
        }
    }

    Box {
        ScreenColumn(
            navigationType = TypeTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.statistic_diagram_title)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DropdownCheckboxMenu(
                    title = stringResource(R.string.measurements),
                    items = state.measurements,
                    onToggle = { onEvent(StatisticDiagramEvent.OnToggleMeasure(it)) },
                    onInfoClick = {
                        onEvent(StatisticDiagramEvent.OnMessage(R.string.dropdown_checkbox_description))
                    }
                )
                MultipleChart(
                    title = stringResource(R.string.grown_up_trainings),
                    onInfoClick = { onEvent(StatisticDiagramEvent.OnMessage(R.string.diagram_measurement)) },
                    axisData = remember(state.measurementsData) { ChartDataUtils.createAxisData(state.measurementsData) }
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DropdownCheckboxMenu(
                        title = stringResource(R.string.sports_exercises_title),
                        items = state.exercises,
                        onToggle = { onEvent(StatisticDiagramEvent.OnToggleExercise(it)) },
                        onInfoClick = { onEvent(StatisticDiagramEvent.OnMessage(R.string.dropdown_checkbox_description)) }
                    )
                    MultipleChart(
                        title = stringResource(R.string.grown_up_trainings),
                        onInfoClick = { onEvent(StatisticDiagramEvent.OnMessage(R.string.diagram_exercise)) },
                        axisData = remember(state.exercisesData) { ChartDataUtils.createAxisData(state.exercisesData) }
                    )
                }

                Spacer(Modifier.height(200.dp))
            }
        }

        CustomSnackbar(snackbarHostState)
    }
}