package com.sinya.projects.sportsdiary.presentation.statistic

import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.core.domain.model.RadioItem
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.presentation.statistic.components.StatisticPlaceholder
import com.sinya.projects.sportsdiary.sheets.BodyInfoSheetContent
import com.sinya.projects.sportsdiary.ui.features.CardWithArrow
import com.sinya.projects.sportsdiary.ui.features.CustomSnackbar
import com.sinya.projects.sportsdiary.ui.features.RadioButtons
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.StatisticCard
import com.sinya.projects.sportsdiary.ui.features.chart.MultipleChart
import com.sinya.projects.sportsdiary.ui.features.chart.utils.ChartDataUtils
import com.sinya.projects.sportsdiary.ui.features.map.BodyCard

@Composable
fun StatisticScreen(
    onBackClick: () -> Unit,
    viewModel: StatisticViewModel = hiltViewModel(),
    navigateToStatisticDiagram: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        StatisticUiState.Loading -> StatisticPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(R.string.statistic_title)
        )

        is StatisticUiState.Success -> {
            StatisticScreenView(
                onBackClick = onBackClick,
                navigateToStatisticDiagram = navigateToStatisticDiagram,
                state = state as StatisticUiState.Success,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
private fun StatisticScreenView(
    onBackClick: () -> Unit,
    navigateToStatisticDiagram: () -> Unit,
    state: StatisticUiState.Success,
    onEvent: (StatisticEvent) -> Unit,
    radioOptions: List<RadioItem<TypeTime>> = listOf(
        RadioItem(stringResource(R.string.days), null, TypeTime.DAYS),
        RadioItem(stringResource(R.string.months), null, TypeTime.MONTHS),
        RadioItem(stringResource(R.string.years), null, TypeTime.YEARS)
    ),
    fontStyle: TextStyle = MaterialTheme.typography.displayLarge,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var fontSize by remember { mutableStateOf(fontStyle.fontSize) }
    val onTextLayout: (TextLayoutResult) -> Unit = remember {
        { result ->
            if (result.didOverflowWidth) {
                fontSize = (fontSize.value * 0.9f).sp.value
                    .coerceAtLeast(12.sp.value).sp
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = context.getString(message),
                duration = SnackbarDuration.Short
            )
            onEvent(StatisticEvent.OnMessage(null))
        }
    }


    Box {
        ScreenColumn(
            navigationType = TypeTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.statistic_title)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatisticCard(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    title = stringResource(R.string.count_of_training),
                    count = state.countTrain.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    title = stringResource(R.string.all_weight),
                    count = state.countWeight.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    title = stringResource(R.string.count_completed_mornings),
                    count = state.countMorning.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    title = stringResource(R.string.series_of_completed_mornings),
                    count = state.seriesScope.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButtons(
                    radioOptions = radioOptions,
                    selectedOption = state.timeMode,
                    onOptionSelected = { mode -> onEvent(StatisticEvent.OnSelectTimePeriod(mode)) },
                    shape = MaterialTheme.shapes.extraLarge
                )
                MultipleChart(
                    title = stringResource(R.string.effect_of_training),
                    onInfoClick = { onEvent(StatisticEvent.OnMessage(R.string.diagram_training)) },
                    axisData = remember(state.chartList) {
                        ChartDataUtils.createAxisDataWithDate(
                            state.chartList,
                            state.timeMode
                        )
                    },
                )
            }

            BodyCard(
                list = state.dataExerciseMuscles,
                withText = false,
                onInfoClick = { onEvent(StatisticEvent.OnBodyInfoClick()) }
            )

            CardWithArrow(
                title = stringResource(R.string.statistic_diagram_title),
                onClick = navigateToStatisticDiagram
            )

            Spacer(Modifier.height(80.dp))
        }

        CustomSnackbar(snackbarHostState)
    }

    if (state.bodyVisible) {
        BodyInfoSheetContent(
            onDismiss = { onEvent(StatisticEvent.OnBodyInfoClick(false)) },
            listMuscles = state.allMuscles
        )
    }
}

