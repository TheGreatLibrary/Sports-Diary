package com.sinya.projects.sportsdiary.presentation.sportExercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.AnimationCard

@Composable
fun SportExercisesScreen(
    vm: SportExercisesViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onExerciseClick: (Int) -> Unit
) {
    when (val state = vm.state.value) {
        is SportExercisesUiState.Loading -> PlaceholderScreen()
        is SportExercisesUiState.Success -> SportExercisesView(
            state = state,
            onEvent = vm::onEvent,
            onBackClick = onBackClick,
            onExerciseClick = onExerciseClick
        )
        is SportExercisesUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun SportExercisesView(
    state: SportExercisesUiState.Success,
    onEvent: (SportExercisesUiEvent) -> Unit,
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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.exercises) {
                AnimationCard(
                    onClick = { onExerciseClick(it.exerciseId) },

                    ) {
                    Column(
                        modifier = Modifier.padding(20.dp),

                    ) {
                        Text(
                            text = it.name
                        )
                      
                    }

                }
            }
        }


    }
}