package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomDropdownMenu
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.TrainingCard
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.TrainingCategorySheet
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises.TrainingExerciseSheet
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.DateCard
import com.sinya.projects.sportsdiary.ui.features.DatePickerModal
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog

@Composable
fun TrainingPageScreen(
    id: Int?,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: TrainingPageViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        TrainingPageUiState.Loading -> PlaceholderScreen()

        is TrainingPageUiState.TrainingForm -> TrainingPage(
            state = state as TrainingPageUiState.TrainingForm,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick
        )

        is TrainingPageUiState.Error -> ErrorScreen((state as TrainingPageUiState.Error).errorMessage)

        TrainingPageUiState.Success -> {
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }
    }
}

@Composable
private fun TrainingPage(
    state: TrainingPageUiState.TrainingForm,
    onEvent: (TrainingPageEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(TrainingPageEvent.OnErrorShown)
        }
    }

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(Modifier.height(40.dp))
                NavigationTopBar(
                    type = TypeAppTopNavigation.WithIcon(
                        onBackClick = onBackClick,
                        title = stringResource(R.string.training_num, state.item.title),
                        painter = R.drawable.nav_save,
                        onClick = { onEvent(TrainingPageEvent.Save) }
                    )
                )
            }

            item {
                DateCard(
                    onDateClick = { onEvent(TrainingPageEvent.CalendarState(true)) },
                    date = state.item.date
                )
                Spacer(Modifier.height(8.dp))
                CustomDropdownMenu(
                    items = state.categories,
                    title = stringResource(R.string.constructor),
                    onInfoClick = { },
                    selectedItem = state.item.category,
                    onOpenMenu = { onEvent(TrainingPageEvent.UpdateCategories) },
                    onSelectedCategory = { name -> onEvent(TrainingPageEvent.OnSelectedCategory(name)) },
                    onPlusClick = { onEvent(TrainingPageEvent.OpenBottomSheetCategory(true)) }
                )

            }

            items(
                items = state.item.items,
                key = { it.id }
            ) { item ->
               TrainingCard(
                   it = item,
                   onInfoClick = { id -> onEvent(TrainingPageEvent.OpenDialog(id)) },
                   onPlusClick = { id -> onEvent(TrainingPageEvent.AddSet(id)) },
                   onMinusClick = { id -> onEvent(TrainingPageEvent.Delete(id)) },
                   onEditSet = { id, index, value, valState ->
                       onEvent(
                           TrainingPageEvent.EditSet(
                               id,
                               index,
                               value,
                               valState
                           )
                       )
                   },
                   onDeleteSet = { id, index ->
                       onEvent(TrainingPageEvent.DeleteSet(id, index))
                   }
               )
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CustomButton(
                        onClick = { onEvent(TrainingPageEvent.OpenBottomSheetTraining(true)) },
                        text = stringResource(R.string.add_exercise),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.height(80.dp))
            }
        }

        state.dialogContent?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(TrainingPageEvent.OpenDialog(null))
                },
                content = {
                    GuideDescriptionView(
                        title = it.name,
                        description = it.description,
                        image = null
                    )
                }
            )
        }

        if (state.bottomSheetCategoryStatus) {
            TrainingCategorySheet(
                onDismiss = {
                    onEvent(TrainingPageEvent.OpenBottomSheetCategory(false))
                }
            )
        }

        if (state.calendarVisible) {
            DatePickerModal(
                onDateSelected = { date -> onEvent(TrainingPageEvent.OnPickDate(date)) },
                onDismiss = { onEvent(TrainingPageEvent.CalendarState(false)) }
            )
        }

        if (state.bottomSheetTrainingStatus) {
            TrainingExerciseSheet(
                id = state.item.id ?: 0,
                onDismiss = { onEvent(TrainingPageEvent.UpdateListTraining(state.item.id)) }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

