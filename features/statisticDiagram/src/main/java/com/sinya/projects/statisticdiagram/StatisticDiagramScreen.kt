package com.sinya.projects.statisticdiagram

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.statisticdiagram.components.MultiChartWithDropdown
import com.sinya.projects.statisticdiagram.components.StatisticDiagramPlaceholder
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.chart.utils.ChartDataUtils
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.CustomSnackbar

@Composable
fun StatisticDiagramScreen(onBackClick: () -> Unit) {
    val viewModel: StatisticDiagramViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        StatisticDiagramUiState.Loading -> StatisticDiagramPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(com.sinya.projects.ui.R.string.statistic_diagram_title)
        )

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
                title = stringResource(com.sinya.projects.ui.R.string.statistic_diagram_title)
            )
        ) {
            MultiChartWithDropdown(
                title = stringResource(R.string.measurements),
                items = state.measurements,
                onToggle = { onEvent(StatisticDiagramEvent.OnToggleMeasure(it)) },
                onInfoClick = {
                    onEvent(StatisticDiagramEvent.OnMessage(R.string.dropdown_checkbox_description))
                },
                onChartInfoClick = { onEvent(StatisticDiagramEvent.OnMessage(com.sinya.projects.ui.R.string.diagram_measurement)) },
                axisData = remember(state.measurementsData) { ChartDataUtils.createAxisData(state.measurementsData) }
            )
            MultiChartWithDropdown(
                title = stringResource(R.string.sports_exercises_title),
                items = state.exercises,
                onToggle = { onEvent(StatisticDiagramEvent.OnToggleExercise(it)) },
                onInfoClick = { onEvent(StatisticDiagramEvent.OnMessage(R.string.dropdown_checkbox_description)) },
                onChartInfoClick = { onEvent(StatisticDiagramEvent.OnMessage(com.sinya.projects.ui.R.string.diagram_training)) },
                axisData = remember(state.exercisesData) { ChartDataUtils.createAxisData(state.exercisesData) }
            )
            Spacer(Modifier.height(200.dp))
        }
        CustomSnackbar(snackbarHostState)
    }
}