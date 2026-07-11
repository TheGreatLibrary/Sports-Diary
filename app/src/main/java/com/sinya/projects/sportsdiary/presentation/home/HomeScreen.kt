package com.sinya.projects.sportsdiary.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.navigation.ScreenRoute
import com.sinya.projects.sportsdiary.presentation.home.components.HomeCard
import com.sinya.projects.sportsdiary.presentation.home.components.HomePlaceholder
import com.sinya.projects.sportsdiary.presentation.home.components.MiniHomeCard
import com.sinya.projects.sportsdiary.presentation.home.components.TrainingHomeCard
import com.sinya.projects.sportsdiary.presentation.home.components.calendar.Calendar
import com.sinya.projects.sportsdiary.ui.features.CustomSnackbar
import com.sinya.projects.sportsdiary.ui.features.CustomTextField

@Composable
fun HomeScreen(
    navigateTo: (ScreenRoute) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        HomeUiState.Loading -> HomePlaceholder()

        is HomeUiState.Success -> {
            HomeScreenView(
                state = currentState,
                onEvent = viewModel::onEvent,
                navigateTo = navigateTo
            )
        }
    }
}

@Composable
private fun HomeScreenView(
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Calendar(
                onExtended = { onEvent(HomeEvent.OnExtended(!state.calendarExpanded)) },
                onButtonMorningClick = { date, morningState ->
                    onEvent(
                        HomeEvent.OnButtonMorningClick(
                            date,
                            morningState,
                            state.currentPlanId
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TrainingHomeCard(
                    list = state.trainingList,
                    onCardClick = { navigateTo(ScreenRoute.Training) },
                    onPlusClick = { navigateTo(ScreenRoute.TrainingPage(null, state.date.toString())) },
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
                        title = stringResource(R.string.sports_exercises_title),
                        description = stringResource(R.string.list_of_exercises),
                        list = emptyList(),
                        onCardClick = { navigateTo(ScreenRoute.SportExercises) },
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

                HomeCard(
                    modifier = Modifier.heightIn(max = 350.dp),
                    title = stringResource(R.string.note_of_morning_exercises),
                    colorCard = MaterialTheme.colorScheme.primaryContainer,
                    colorText = MaterialTheme.colorScheme.onPrimary,
                    icon = painterResource(R.drawable.nav_save),
                    onCardClick = {  },
                    onPlusClick = { onEvent(HomeEvent.SaveNote) }
                ) {
                    CustomTextField(
                        value = state.monthDays.find { it.date == state.date }?.note ?: "",
                        onValueChange = { s -> onEvent(HomeEvent.OnValueChange(s)) },
                        onTrailingClick = { onEvent(HomeEvent.OnValueChange("")) },
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth(),
                        imeAction = ImeAction.Done,
                        singleLine = false,
                        height = 200.dp,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
                Spacer(Modifier.height(80.dp))
            }
        }

        CustomSnackbar(snackbarHostState)
    }
}