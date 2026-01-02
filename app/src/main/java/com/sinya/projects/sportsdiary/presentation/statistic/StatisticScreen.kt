package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.home.HomeEvent
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.RadioButtons
import com.sinya.projects.sportsdiary.ui.features.StatisticCard
import com.sinya.projects.sportsdiary.ui.features.diagram.Chart
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDiagramView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import java.util.Locale

@Composable
fun StatisticScreen(
    onBackClick: () -> Unit,
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        StatisticUiState.Loading -> PlaceholderScreen()

        is StatisticUiState.Success -> {
            StatisticScreenView(
                onBackClick = onBackClick,
                state = state as StatisticUiState.Success,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun StatisticScreenView(
    onBackClick: () -> Unit,
    state: StatisticUiState.Success,
    onEvent: (StatisticEvent) -> Unit,
    radioOptions: List<String> = listOf(
        stringResource(R.string.days),
        stringResource(R.string.months),
        stringResource(R.string.years)
    ),
    fontStyle: TextStyle = MaterialTheme.typography.displayLarge
) {
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
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(StatisticEvent.OnErrorShown)
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 50.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationTopBar(
                type = TypeAppTopNavigation.WithoutIcon(
                    onBackClick = onBackClick,
                    title = stringResource(R.string.statistic_title)
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatisticCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    title = stringResource(R.string.count_of_training),
                    count = state.countTrain.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    title = stringResource(R.string.all_weight),
                    count = state.countWeight.toString(),
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
                    selectedOption = state.timeMode.index,
                    onOptionSelected = { index -> onEvent(StatisticEvent.OnSelectTimePeriod(index)) },
                    shape = MaterialTheme.shapes.extraLarge
                )
                Chart(
                    title = stringResource(R.string.effect_of_training),
                    onInfoClick = { onEvent(StatisticEvent.OnDialogState(true)) },
                    timeMode = state.timeMode,
                    points = state.chartList
                )
            }

            if (state.dialogState) {
                GuideDialog(
                    onDismiss = { onEvent(StatisticEvent.OnDialogState(false)) },
                    content = {
                        GuideDiagramView(
                            title = stringResource(R.string.note),
                            image = if (Locale.getDefault()
                                    .toString() == "ru"
                            ) painterResource(R.drawable.graph_guide_ru)
                            else painterResource(R.drawable.graph_guide_en)
                        )
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
        )
    }

}