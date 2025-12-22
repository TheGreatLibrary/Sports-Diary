package com.sinya.projects.sportsdiary.presentation.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises.TrainingExerciseEvent
import com.sinya.projects.sportsdiary.ui.features.AnimationCard
import com.sinya.projects.sportsdiary.ui.features.CustomTextField

@Composable
fun ExercisesScreen(
    vm: ExercisesViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onExerciseClick: (Int) -> Unit
) {
    when (val state = vm.state.value) {
        is ExercisesUiState.Loading -> PlaceholderScreen()
        is ExercisesUiState.Success -> ExercisesView(
            query = state.query,
            exercises = vm.filtered(),
            onEvent = vm::onEvent,
            onBackClick = onBackClick,
            onExerciseClick = onExerciseClick
        )

        is ExercisesUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun ExercisesView(
    query: String,
    exercises: List<ExerciseTranslations>,
    onEvent: (ExercisesEvent) -> Unit,
    onBackClick: () -> Unit,
    onExerciseClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = stringResource(R.string.sport_exercises_title),
            isVisibleBack = true,
            onBackClick = onBackClick
        )
        Text(
            text = stringResource(R.string.search),
            style = MaterialTheme.typography.titleMedium
        )
        CustomTextField(
            value = query,
            onValueChange = { s ->
                onEvent(
                    ExercisesEvent.OnQueryChange(
                        s
                    )
                )
            },
            onTrailingClick = {
                onEvent(
                    ExercisesEvent.OnQueryChange(
                        ""
                    )
                )
            },
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        if (exercises.isEmpty()) {
            Text(
                text = stringResource(R.string.nothing_found),
                modifier = Modifier.padding(16.dp)
            )
        }
        else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(exercises) {
                    AnimationCard(
                        onClick = { onExerciseClick(it.exerciseId) }
                    ) {
                        Box(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}