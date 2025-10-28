package com.sinya.projects.sportsdiary.presentation.exercisePage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.repository.ExerciseMusclesData
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen

@Composable
fun ExercisePageScreen(
    state: ExercisePageUiState,
    onEvent: (ExercisePageEvent) -> Unit,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    when (state) {
        is ExercisePageUiState.Loading -> PlaceholderScreen()
        is ExercisePageUiState.Success -> ExercisePageView(
            item = state.exercise,
            itemsM = state.exMuscles,
            onEvent = onEvent,
            onBackClick = onBackClick
        )
        is ExercisePageUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun ExercisePageView(
    item: ExerciseTranslations,
    itemsM: List<ExerciseMusclesData>,
    onEvent: (ExercisePageEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
        Text(
            text = "Уровень: ${item.level?: stringResource(R.string.not_found_data)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = "Тип: ${item.force?: stringResource(R.string.not_found_data)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = "Экиперовка: ${item.equipment?: stringResource(R.string.not_found_data)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = "Категория: ${item.category?: stringResource(R.string.not_found_data)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = "Механика: ${item.mechanic?: stringResource(R.string.not_found_data)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        val primaryM = itemsM.filter { it.value == 2 }.joinToString(", ") { it.name }
        val secondaryM = itemsM.filter { it.value == 1 }.joinToString(", ") { it.name }
        Text(
            text = "Главные мышцы: $primaryM",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = "побочные  мышцы: $secondaryM",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        ///U8M2BX7rW12XfcUN0XU0wqeUHuDUr6IsidVIMiLomPtTmd9B4ZxAyspY
    }
}