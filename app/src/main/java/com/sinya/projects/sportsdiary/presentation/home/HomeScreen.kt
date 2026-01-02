package com.sinya.projects.sportsdiary.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.main.ScreenRoute
import com.sinya.projects.sportsdiary.presentation.home.calendar.Calendar
import com.sinya.projects.sportsdiary.presentation.home.components.MiniHomeCard
import com.sinya.projects.sportsdiary.presentation.home.components.TrainingHomeCard
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen

@Composable
fun HomeScreen(
    currentPlanId: Int,
    navigateTo: (ScreenRoute) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.loadMonth()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        HomeUiState.Loading -> PlaceholderScreen()

        is HomeUiState.Success -> {
            HomeScreenView(
                currentPlanId = currentPlanId,
                state = currentState,
                onEvent = viewModel::onEvent,
                navigateTo = navigateTo
            )
        }
    }
}

@Composable
private fun HomeScreenView(
    currentPlanId: Int,
    state: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(HomeEvent.OnErrorShown)
        }
    }

    Box {
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
                onButtonMorningClick = { date, state ->
                    onEvent(
                        HomeEvent.OnButtonMorningClick(
                            date,
                            state,
                            currentPlanId
                        )
                    )
                },
                onShift = { ind -> onEvent(HomeEvent.OnShift(ind)) },
                pickDay = { date -> onEvent(HomeEvent.PickDay(date)) },
                state = state
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TrainingHomeCard(
                    list = state.trainingList,
                    onCardClick = { navigateTo(ScreenRoute.Training) },
                    onPlusClick = { navigateTo(ScreenRoute.TrainingPage()) },
                    onItemClick = { id -> navigateTo(ScreenRoute.TrainingPage(id)) },
                )
                Row(
                    modifier = Modifier.height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    MiniHomeCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = stringResource(R.string.morning_exercises_title),
                        description = stringResource(R.string.morning_exercises_description),
                        list = emptyList(),
                        onCardClick = { navigateTo(ScreenRoute.MorningExercises) },
                        onItemClick = {},
                        colorCard = MaterialTheme.colorScheme.primary
                    )
                    MiniHomeCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = stringResource(R.string.proportions_title),
                        description = stringResource(R.string.proportions_description),
                        list = emptyList(),
                        onCardClick = { navigateTo(ScreenRoute.Proportions) },
                        onPlusClick = { navigateTo(ScreenRoute.ProportionPage()) },
                        onItemClick = {},
                        colorCard = MaterialTheme.colorScheme.tertiary

                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
        )
    }
}