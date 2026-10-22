package com.sinya.projects.pagetraining.analyze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.pagetraining.form.components.TrainingPagePlaceholder
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.DateCard
import com.sinya.projects.ui.components.StatisticCard
import com.sinya.projects.ui.components.map.BodyCard
import com.sinya.projects.ui.components.screen.ScreenLazyColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.BodyInfoSheetContent
import com.sinya.projects.ui.theme.LocalDateFormatDMY
import java.time.LocalDate


@Composable
internal fun TrainingAnalyze(
    id: Int?,
    onBackClick: () -> Unit,
) {
    val viewModel: TrainingAnalyzeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadData(id)
        }
    }

    when (state) {
        TrainingAnalyzeUiState.Loading -> TrainingPagePlaceholder("", onBackClick)

        is TrainingAnalyzeUiState.Success -> {
            TrainingAnalyzeView(
                state = state as TrainingAnalyzeUiState.Success,
                onEvent = viewModel::onEvent,
                onBackClick = onBackClick,
            )
        }
    }
}

@Composable
private fun TrainingAnalyzeView(
    state: TrainingAnalyzeUiState.Success,
    onEvent: (TrainingAnalyzeEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val formatter = LocalDateFormatDMY.current
    val currentDate = remember(state.date) { LocalDate.parse(state.date.toString()).format(formatter) }

    val fontStyle2 = MaterialTheme.typography.displaySmall
    var fontSize2 by remember { mutableStateOf(fontStyle2.fontSize) }
    val onTextLayout2: (TextLayoutResult) -> Unit = remember {
        { result ->
            if (result.didOverflowWidth) {
                fontSize2 = (fontSize2.value * 0.9f).sp.value
                    .coerceAtLeast(8.sp.value).sp
            }
        }
    }

    val fontStyle = MaterialTheme.typography.displayLarge
    var fontSize by remember { mutableStateOf(fontStyle.fontSize) }
    val onTextLayout: (TextLayoutResult) -> Unit = remember {
        { result ->
            if (result.didOverflowWidth) {
                fontSize = (fontSize.value * 0.9f).sp.value
                    .coerceAtLeast(12.sp.value).sp
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
    ) {
        ScreenLazyColumn(
            arrangement = Arrangement.spacedBy(8.dp),
            navigationType = TypeTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.training_title),
            )
        ) {
            item {
                Text(
                    text = stringResource(R.string.date_at, currentDate),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Start
                )
            }

            item {
                Text(
                    text = "${stringResource(com.sinya.projects.pagetraining.R.string.routine)}: ${state.category?.name ?: stringResource(R.string.not_category)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Start
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatisticCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = stringResource(R.string.all_weight),
                        count = state.summaryWeight.toString(),
                        fontSize = fontSize,
                        onTextLayout = onTextLayout
                    )
                    StatisticCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = stringResource(com.sinya.projects.pagetraining.R.string.effect),
                        count = "${state.coefficient.toInt()}%",
                        fontSize = fontSize,
                        onTextLayout = onTextLayout
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatisticCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = stringResource(com.sinya.projects.pagetraining.R.string.count_ex),
                        count = state.countEx.toString(),
                        fontSize = fontSize2,
                        onTextLayout = onTextLayout2
                    )
                    StatisticCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = stringResource(com.sinya.projects.pagetraining.R.string.count_set),
                        count = state.countSet.toString(),
                        fontSize = fontSize2,
                        onTextLayout = onTextLayout2
                    )
                    StatisticCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = stringResource(com.sinya.projects.pagetraining.R.string.count_of_step),
                        count = state.countRep.toString(),
                        fontSize = fontSize2,
                        onTextLayout = onTextLayout2
                    )
                }
            }

            item {
                BodyCard(
                    title = stringResource(R.string.effective_muscles),
                    list = state.dataExerciseMuscles,
                    withText = false,
                    onInfoClick = { onEvent(TrainingAnalyzeEvent.OnBodyInfoClick) }
                )
            }
        }

        if (state.bodyVisible) {
            CustomModalSheet(
                onDismiss = { onEvent(TrainingAnalyzeEvent.OnBodyInfoClick) },
                content = { BodyInfoSheetContent(listMuscles = state.allMuscles) }
            )
        }
    }
}