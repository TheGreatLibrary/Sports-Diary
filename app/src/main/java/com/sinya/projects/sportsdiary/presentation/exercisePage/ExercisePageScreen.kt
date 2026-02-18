package com.sinya.projects.sportsdiary.presentation.exercisePage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.exercisePage.components.ExercisePageView
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen

@Composable
fun ExercisePageScreen(
    id: Int?,
    onBackClick: () -> Unit,
    navigateToEdit: (Int) -> Unit
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: ExercisePageViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ExercisePageUiState.Loading -> PlaceholderScreen()

        is ExercisePageUiState.Success -> ExercisePageView(
            state = state as ExercisePageUiState.Success,
            onBackClick = onBackClick,
            navigateToEdit = navigateToEdit
        )

        is ExercisePageUiState.Error -> ErrorScreen((state as ExercisePageUiState.Error).message)
    }
}
