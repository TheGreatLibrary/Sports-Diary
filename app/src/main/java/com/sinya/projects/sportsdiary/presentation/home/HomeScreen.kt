package com.sinya.projects.sportsdiary.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.home.components.Calendar
import com.sinya.projects.sportsdiary.presentation.home.components.CardHome
import com.sinya.projects.sportsdiary.presentation.home.components.TrainingCard
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.AnimationCard

@Composable
fun HomeScreen(
    currentPlanId: Int,
    vm: HomeViewModel = hiltViewModel(),
    onTrainingClick: () -> Unit,
    onTrainingPlusClick: () -> Unit,
    onTrainingCardClick: (Int) -> Unit,
    onMorningExercisesClick: () -> Unit,
    onProportionsClick: () -> Unit,
    onProportionsPlusClick: () -> Unit
) {
    when (val state = vm.state.value) {
        is HomeScreenUiState.Loading -> PlaceholderScreen()
        is HomeScreenUiState.Success -> {
            HomeScreenView(
                currentPlanId = currentPlanId,
                state = state,
                onEvent = vm::onEvent,
                onTrainingClick = onTrainingClick,
                onTrainingPlusClick = onTrainingPlusClick,
                onTrainingCardClick = onTrainingCardClick,
                onMorningExercisesClick = onMorningExercisesClick,
                onProportionsClick = onProportionsClick,
                onProportionsPlusClick = onProportionsPlusClick
            )
        }
        is HomeScreenUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun HomeScreenView(
    currentPlanId: Int,
    state: HomeScreenUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    onTrainingClick: () -> Unit,
    onTrainingPlusClick: () -> Unit,
    onTrainingCardClick: (Int) -> Unit,
    onMorningExercisesClick: () -> Unit,
    onProportionsClick: () -> Unit,
    onProportionsPlusClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 150.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Calendar(
            onExtended = { onEvent(HomeEvent.OnExtended(!state.calendarExpanded)) },
            onButtonMorningClick = { onEvent(HomeEvent.OnButtonMorningClick(!state.morningState, currentPlanId)) },
            onShift = { ind -> onEvent(HomeEvent.OnShift(ind)) },
            morningState = state.morningState,
            daysNumber = state.monthDays,
            expandedCalendar = state.calendarExpanded,
            year = state.year,
            month = state.month
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TrainingCard(
                trainingList = state.trainingList,
                onTrainingClick = onTrainingClick,
                onTrainingPlusClick = onTrainingPlusClick,
                onTrainingCardClick = onTrainingCardClick
            )
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                AnimationCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = onMorningExercisesClick,
                    colorCard = MaterialTheme.colorScheme.primary
                ) {
                    CardHome(
                        title = stringResource(R.string.morning_exercises_title),
                        onPlusClick = {  },
                        plusVisible = false,
                        content = {
                            Text(
                                text = stringResource(R.string.morning_exercises_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    )
                }
                AnimationCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = onProportionsClick,
                    colorCard = MaterialTheme.colorScheme.tertiary
                ) {
                    CardHome(
                        title = stringResource(R.string.proportions_title),
                        onPlusClick = onProportionsPlusClick,
                        content = {
                            Text(
                                text = stringResource(R.string.proportions_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    )
                }
            }
        }
    }
}