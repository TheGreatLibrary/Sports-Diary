package com.sinya.projects.sportsdiary.presentation.sportExercisePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen

@Composable
fun SportExercisePageScreen(
    state: SportExercisePageUiState,
    onEvent: (SportExercisePageUiEvent) -> Unit,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    when (state) {
        is SportExercisePageUiState.Loading -> PlaceholderScreen()
        is SportExercisePageUiState.Success -> SportExercisePageView(
//            title = state.item.title,
//            onInfoClick = onInfoClick,
//            onBackClick = onBackClick,
//            proportion = state.item,
            item = state.exercise,
            onEvent = onEvent,
            onBackClick = onBackClick
        )
        is SportExercisePageUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun SportExercisePageView(
    item: ExerciseTranslations,
    onEvent: (SportExercisePageUiEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = "",
            isVisibleBack = true,
            onBackClick = onBackClick
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = item.rule,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = stringResource(R.string.illustration),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )

        ///U8M2BX7rW12XfcUN0XU0wqeUHuDUr6IsidVIMiLomPtTmd9B4ZxAyspY
    }
}