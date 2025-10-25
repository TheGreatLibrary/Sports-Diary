package com.sinya.projects.sportsdiary.presentation.morningExercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote.ModalSheetNote
import com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan.ModalSheetPlan
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.statistic.components.StatCard
import com.sinya.projects.sportsdiary.ui.features.CardWithArrow

@Composable
fun MorningExercisesScreen(
    vm: MorningExercisesViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    when (val state = vm.state.value) {
        is MorningExercisesUiState.Loading -> PlaceholderScreen()
        is MorningExercisesUiState.Success -> MorningExercisesView(
            state = state,
            onEvent = vm::onEvent,
            onBackClick = onBackClick
        )

        is MorningExercisesUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun MorningExercisesView(
    state: MorningExercisesUiState.Success,
    onEvent: (MorningExercisesUiEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = stringResource(R.string.morning_exercises_title),
            isVisibleBack = true,
            onBackClick = onBackClick
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.count_of_training),
                count = state.countTraining.toString()
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.all_weight),
                count = state.seriesScope.toString()
            )
        }
        CardWithArrow(
            title = stringResource(R.string.data_of_morning_exercises),
            onClick = {
                onEvent(MorningExercisesUiEvent.OnPlanExpanded(true))
            }
        )
        CardWithArrow(
            title = stringResource(R.string.note_of_morning_exercises),
            onClick = {
                onEvent(MorningExercisesUiEvent.OnNoteExpanded(true))
            }
        )

        if (state.noteExpanded) {
            ModalSheetNote(
                onDismiss = { onEvent(MorningExercisesUiEvent.OnNoteExpanded(false))}
            )
        }

        if (state.planExpanded) {
            ModalSheetPlan(
                onDismiss = { onEvent(MorningExercisesUiEvent.OnPlanExpanded(false))}
            )
        }
    }
}

