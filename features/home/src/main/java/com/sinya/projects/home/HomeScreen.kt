package com.sinya.projects.home

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.HealthProgressData
import com.sinya.projects.home.components.HomePlaceholder
import com.sinya.projects.home.components.calendar.Calendar
import com.sinya.projects.home.components.card.HomeCard
import com.sinya.projects.home.components.card.NoteCard
import com.sinya.projects.home.components.healthPager.HealthRings
import com.sinya.projects.ui.components.CustomSnackbar
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun HomeScreen(
    navigateToTrainings: () -> Unit,
    navigateToTrainingPage: (Int?, String?) -> Unit,
    navigateToMeasures: () -> Unit,
    navigateToNotes: () -> Unit,
    navigateToMeasurePage: (Int?, String?) -> Unit,
    navigateToHealth: () -> Unit
) {

    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        HomeUiState.Loading -> HomePlaceholder()

        is HomeUiState.Success -> {
            HomeScreenView(
                state = currentState,
                onEvent = viewModel::onEvent,
                navigateToTrainings = navigateToTrainings,
                navigateToTrainingPage = navigateToTrainingPage,
                navigateToMeasures = navigateToMeasures,
                navigateToNotes = navigateToNotes,
                navigateToHealth = navigateToHealth,
                navigateToMeasurePage = navigateToMeasurePage
            )
        }
    }
}

@Composable
private fun HomeScreenView(
    state: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    navigateToTrainings: () -> Unit,
    navigateToTrainingPage: (Int?, String?) -> Unit,
    navigateToMeasures: () -> Unit,
    navigateToNotes: () -> Unit,
    navigateToHealth: () -> Unit,
    navigateToMeasurePage: (Int?, String?) -> Unit,
) {
    val gradients = LocalAppGradients.current
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
                today = state.date,
                days = state.monthDays,
                calendarExpanded = state.calendarExpanded,
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
                navigateToNotes = navigateToNotes,
                onToggleListOfMorningDays = { onEvent(HomeEvent.ToggleListOfMorningDays) },
                onCheckClick = { onEvent(HomeEvent.OnCheckClick(it)) },
                onShift = { ind -> onEvent(HomeEvent.OnShift(ind)) },
                pickDay = { date -> onEvent(HomeEvent.PickDay(date)) },
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeCard(
                    list = state.trainingList,
                    title = stringResource(com.sinya.projects.ui.R.string.trainings_title),
                    onItemClick = { item -> navigateToTrainingPage(item.id, null) },
                    description = stringResource(R.string.home_no_data_of_training),
                    descriptionAlign = TextAlign.Center,
                    getText = { item ->
                        stringResource(
                            R.string.home_training_item_description,
                            item.category ?: stringResource(com.sinya.projects.ui.R.string.not_category),
                            item.name,
                            item.date
                        )
                    },
                    backgroundColor = gradients.trainingCardGradient,
                    backgroundItemColor = gradients.trainingCardItemGradient,
                    onCardClick = navigateToTrainings,
                    onIconClick = { navigateToTrainingPage(null, state.date.date.toString()) }
                )
                Row(
                    modifier = Modifier.heightIn(max = 160.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HealthRings(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        list = listOf(
                            HealthProgressData(
                                stringResource(R.string.steps),
                                R.drawable.health_foot,
                                Color.valueOf(MaterialTheme.colorScheme.primary.value.toLong()),
                                state.stepsToday/10000f,
                                state.stepsToday.toString()
                            ),
                            HealthProgressData(
                                stringResource(R.string.sleep),
                                R.drawable.health_sleep,
                                Color.valueOf(MaterialTheme.colorScheme.tertiary.value.toLong()),
                                state.sleepDurationMinutes/120f,
                                state.sleepDurationMinutes.toString()
                            ),
                            HealthProgressData(
                                stringResource(R.string.kkal),
                                R.drawable.health_calories,
                                Color.valueOf(MaterialTheme.colorScheme.secondary.value.toLong()),
                                state.totalKcalToday/400f,
                                state.totalKcalToday.toString()
                            )
                        ),
                        navigateToHealth = navigateToHealth
                    )
                    HomeCard(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        list = state.proportionList,
                        title = stringResource(com.sinya.projects.ui.R.string.proportions_title),
                        description = stringResource(R.string.home_card_proportion_description),
                        onItemClick = { navigateToMeasurePage(it.id, null) },
                        getText = { item ->
                            stringResource(
                                R.string.home_proportion_item_description,
                                item.id,
                                item.date
                            )
                        },
                        backgroundColor = gradients.measurementCardGradient,
                        backgroundItemColor = gradients.measurementCardItemGradient,
                        onCardClick = navigateToMeasures,
                        onIconClick = { navigateToMeasurePage(null, state.date.date.toString()) }
                    )
                }

                NoteCard(
                    value = state.monthDays.find { it.date == state.date.date }?.note ?: "",
                    onValueChange = { s -> onEvent(HomeEvent.OnValueChange(s)) },
                    onIconClick = { onEvent(HomeEvent.SaveNote) },
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    placeholder = stringResource(R.string.home_card_note_placeholder)
                )

                Spacer(Modifier.height(80.dp))
            }
        }

        CustomSnackbar(snackbarHostState)
    }
}