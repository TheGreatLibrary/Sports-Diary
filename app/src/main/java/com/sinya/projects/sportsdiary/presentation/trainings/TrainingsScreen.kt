package com.sinya.projects.sportsdiary.presentation.trainings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionPageUiEvent
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingPageEvent
import com.sinya.projects.sportsdiary.presentation.trainings.components.RadioIcons
import com.sinya.projects.sportsdiary.presentation.trainings.components.TrainingsByMuscle
import com.sinya.projects.sportsdiary.presentation.trainings.components.TrainingsByTime
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog

@Composable
fun TrainingsScreen(
    vm: TrainingViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTrainingClick: (Int?) -> Unit
) {
    when (val state = vm.state.value) {
        is TrainingUiState.Loading -> PlaceholderScreen()
        is TrainingUiState.Success -> TrainingsScreenView(
            state = state,
            onEvent = vm::onEvent,
            onBackClick = onBackClick,
            onTrainingClick = onTrainingClick
        )

        is TrainingUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun TrainingsScreenView(
    state: TrainingUiState.Success,
    onEvent: (TrainingEvent) -> Unit,
    onBackClick: () -> Unit,
    onTrainingClick: (Int?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            type = TypeAppTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.training_title),
                painter = R.drawable.ic_plus,
                onClick = { onTrainingClick(null) }
            )
        )

        RadioIcons(
            onMuscleClick = { onEvent(TrainingEvent.ModeChange(SortMode.MUSCLE)) },
            onTimeClick = { onEvent(TrainingEvent.ModeChange(SortMode.TIME)) },
            isSelected = SortMode.TIME == state.mode
        )
        when (state.mode) {
            SortMode.TIME -> TrainingsByTime(
                trainings = state.trainings,
                onTrainingClick = onTrainingClick,
                onMinusClick = { id -> onEvent(TrainingEvent.OpenDialog(id)) }
            )

            SortMode.MUSCLE -> TrainingsByMuscle(
                trainings = state.trainings,
                onTrainingClick = onTrainingClick,
                onMinusClick = { id -> onEvent(TrainingEvent.OpenDialog(id)) }
            )
        }

        state.deleteDialogId?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(TrainingEvent.OpenDialog(null))
                },
                content = {
                    DeleteDialogView(
                        onSuccess = { onEvent(TrainingEvent.DeleteTraining) },
                        onBack = { onEvent(TrainingEvent.OpenDialog(null)) }
                    )
                }
            )
        }
    }
}