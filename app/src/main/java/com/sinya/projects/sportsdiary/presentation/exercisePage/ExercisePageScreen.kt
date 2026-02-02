package com.sinya.projects.sportsdiary.presentation.exercisePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun ExercisePageScreen(
    id: Int,
    onBackClick: () -> Unit
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
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick
        )

        is ExercisePageUiState.Error -> ErrorScreen((state as ExercisePageUiState.Error).message)
    }
}
@Composable
private fun ExercisePageView(
    state: ExercisePageUiState.Success,
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
        NavigationTopBar(type = TypeAppTopNavigation.OnlyBackIcon(onBackClick = onBackClick))

        // Заголовок
        Text(
            text = state.exercise.name,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,

        )

        InfoCard(title = stringResource(R.string.characteristics)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CharacteristicRow(
                    label = stringResource(R.string.level),
                    value = state.exercise.level ?: stringResource(R.string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(R.string.type),
                    value = state.exercise.force ?: stringResource(R.string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(R.string.equipment),
                    value = state.exercise.equipment ?: stringResource(R.string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(R.string.category),
                    value = state.exercise.category ?: stringResource(R.string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(R.string.mechanic),
                    value = state.exercise.mechanic ?: stringResource(R.string.not_found_data)
                )
            }
        }

        // Описание и правила
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                )
            Text(
                text = state.exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
                )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.rules),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                )
            Text(
                text = state.exercise.rule,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
                )
        }

        // Иллюстрация
//        Text(
//            text = stringResource(R.string.illustration),
//            style = MaterialTheme.typography.titleLarge,
//            color = MaterialTheme.colorScheme.onPrimary,
//        )


        // Мышцы
        val primaryM = state.exMuscles.filter { it.value == 2 }.joinToString(", ") { it.name }
        val secondaryM = state.exMuscles.filter { it.value == 1 }.joinToString(", ") { it.name }

        InfoCard(title = stringResource(R.string.muscle_title)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (primaryM.isNotEmpty()) {
                    MuscleGroup(
                        label = stringResource(R.string.primary_muscle),
                        muscles = primaryM,
                        isPrimary = true
                    )
                }
                if (secondaryM.isNotEmpty()) {
                    MuscleGroup(
                        label = stringResource(R.string.secondary_muscle),
                        muscles = secondaryM,
                        isPrimary = false
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            content()
        }
    }
}

@Composable
private fun CharacteristicRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
            )
    }
}

@Composable
private fun MuscleGroup(
    label: String,
    muscles: String,
    isPrimary: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isPrimary)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
                )
        }
        Text(
            text = muscles,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}