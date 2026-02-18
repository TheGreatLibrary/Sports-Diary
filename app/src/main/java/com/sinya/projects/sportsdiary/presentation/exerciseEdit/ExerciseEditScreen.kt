package com.sinya.projects.sportsdiary.presentation.exerciseEdit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.components.ExerciseEditView
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen

@Composable
fun ExerciseEditScreen(
    id: Int?,
    onBackClick: () -> Unit,
    navigateToExercisePage: (Int) -> Unit
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: ExerciseEditViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ExerciseEditUiState.Loading -> PlaceholderScreen()

        is ExerciseEditUiState.ExerciseForm -> ExerciseEditView(
            state = state as ExerciseEditUiState.ExerciseForm,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick
        )

        is ExerciseEditUiState.Error -> ErrorScreen((state as ExerciseEditUiState.Error).message)

        is ExerciseEditUiState.Success -> {
            LaunchedEffect(state as ExerciseEditUiState.Success) {
                navigateToExercisePage((state as ExerciseEditUiState.Success).id)
            }
        }
    }
}
