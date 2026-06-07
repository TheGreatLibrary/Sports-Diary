package com.sinya.projects.sportsdiary.presentation.morningExercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote.MorningNoteSheet
import com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan.MorningPlanSheet
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.CardWithArrow
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.StatisticCard

@Composable
fun MorningExercisesScreen(
    vm: MorningExercisesViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val state by vm.state.collectAsStateWithLifecycle()

    when (state) {
        is MorningExercisesUiState.Loading -> PlaceholderScreen()
        is MorningExercisesUiState.Success -> MorningExercisesView(
            state = state as MorningExercisesUiState.Success,
            onEvent = vm::onEvent,
            onBackClick = onBackClick
        )

        is MorningExercisesUiState.Error -> ErrorScreen(
            (state as MorningExercisesUiState.Error).message
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MorningExercisesView(
    state: MorningExercisesUiState.Success,
    onEvent: (MorningExercisesEvent) -> Unit,
    onBackClick: () -> Unit,
    fontStyle: TextStyle = MaterialTheme.typography.displayLarge
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )
    LaunchedEffect(state.noteExpanded) {
        if (state.noteExpanded) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    var fontSize by remember { mutableStateOf(fontStyle.fontSize) }
    val onTextLayout: (TextLayoutResult) -> Unit = remember {
        { result ->
            if (result.didOverflowWidth) {
                fontSize = (fontSize.value * 0.9f).sp.value
                    .coerceAtLeast(12.sp.value).sp
            }
        }
    }

    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = {
            if (state.noteExpanded) {
                MorningNoteSheet(
                    onDismiss = { onEvent(MorningExercisesEvent.OnNoteExpanded(false)) }
                )
            }
            else if (state.planExpanded) {
                MorningPlanSheet(
                    onDismiss = { onEvent(MorningExercisesEvent.OnPlanExpanded(false)) }
                )
            }
        }
    ) {
        ScreenColumn(
            navigationType = TypeAppTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.morning_exercises_title)
            )
        ) {
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
                    title = stringResource(R.string.count_completed_mornings),
                    count = state.countTraining.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
                StatisticCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    title = stringResource(R.string.series_of_completed_mornings),
                    count = state.seriesScope.toString(),
                    fontSize = fontSize,
                    onTextLayout = onTextLayout
                )
            }
            CardWithArrow(
                title = stringResource(R.string.data_of_morning_exercises),
                onClick = {
                    onEvent(MorningExercisesEvent.OnPlanExpanded(true))
                }
            )
            CardWithArrow(
                title = stringResource(R.string.note_of_morning_exercises),
                onClick = {
                    onEvent(MorningExercisesEvent.OnNoteExpanded(true))
                }
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}

