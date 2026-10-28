package com.sinya.projects.statistic

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
import com.sinya.projects.domain.enums.TypeTime
import com.sinya.projects.domain.model.CardItem
import com.sinya.projects.domain.model.RadioItem
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.StatisticCard
import com.sinya.projects.statistic.components.StatisticPlaceholder
import com.sinya.projects.ui.components.ChartSortedRow
import com.sinya.projects.ui.components.sheet.components.BodyInfoSheetContent
import com.sinya.projects.ui.components.ListOfCards
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.CustomSnackbar
import com.sinya.projects.ui.components.chart.MultipleChart
import com.sinya.projects.ui.components.chart.utils.ChartDataUtils
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.map.BodyCard

@Composable
fun StatisticScreen(
    onBackClick: () -> Unit,
    navigateToStatisticDiagram: () -> Unit
) {
    val viewModel: StatisticViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        StatisticUiState.Loading -> StatisticPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(com.sinya.projects.ui.R.string.statistic_title)
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
        RadioItem(stringResource(R.string.statistic_days), null, TypeTime.DAYS),
        RadioItem(stringResource(R.string.statistic_months), null, TypeTime.MONTHS),
        RadioItem(stringResource(R.string.statistic_years), null, TypeTime.YEARS)
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
                title = stringResource(com.sinya.projects.ui.R.string.statistic_title)
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
                    title = stringResource(R.string.statistic_count_of_training),
                    count = state.countTrain.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    title = stringResource(R.string.statistic_all_weight),
                    count = state.countWeight.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    title = stringResource(R.string.statistic_count_mornings),
                    count = state.countMorning.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    title = stringResource(R.string.statistic_series_of_mornings),
                    count = state.seriesScope.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChartSortedRow(
                    radioOptions = radioOptions,
                    selectedOption = state.timeMode,
                    onOptionSelected = { mode -> onEvent(StatisticEvent.OnSelectTimePeriod(mode)) }
                )
                MultipleChart(
                    title = stringResource(R.string.statistic_effect_of_training),
                    onInfoClick = { onEvent(StatisticEvent.OnMessage(com.sinya.projects.ui.R.string.diagram_training)) },
                    axisData = remember(state.chartList) {
                        ChartDataUtils.createAxisDataWithDate(
                            state.chartList,
                            state.timeMode
                        )
                    }
                )
            }

            BodyCard(
                title = when(state.mapState) {
                    true -> stringResource(com.sinya.projects.ui.R.string.work_muscles_for_7_days)
                    else -> stringResource(com.sinya.projects.ui.R.string.work_muscles_for_week)
                },
                list = state.dataExerciseMuscles,
                withText = false,
                onInfoClick = { onEvent(StatisticEvent.OnBodyInfoClick()) }
            )

            ListOfCards(
                list = listOf(CardItem(
                    title = stringResource(com.sinya.projects.ui.R.string.statistic_diagram_title),
                    icon = com.sinya.projects.ui.R.drawable.nav_stat,
                    onClick = navigateToStatisticDiagram
                ))
            )

            Spacer(Modifier.height(80.dp))
        }

        CustomSnackbar(snackbarHostState)
    }

    if (state.bodyVisible) {
        CustomModalSheet(
            onDismiss = { onEvent(StatisticEvent.OnBodyInfoClick(false)) },
            content = { BodyInfoSheetContent(listMuscles = state.allMuscles) }
        )
    }
}

