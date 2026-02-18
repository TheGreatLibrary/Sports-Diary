package com.sinya.projects.sportsdiary.presentation.exercisePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.exercisePage.ExercisePageUiState
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.components.CharacteristicRow
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.components.InfoCard

@Composable
fun ExercisePageView(
    state: ExercisePageUiState.Success,
    onBackClick: () -> Unit,
    navigateToEdit: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            type = if (state.exercise.isCustom) TypeAppTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = "",
                painter = R.drawable.icon_edit,
                onClick = { navigateToEdit(state.exercise.id) }
            ) else TypeAppTopNavigation.WithoutIcon(onBackClick = onBackClick, title = "")
        )

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


        val primaryM = remember {
            state.exMuscles.filter { it.value == 2 }.joinToString(", ") { it.name }
        }
        val secondaryM = remember {
            state.exMuscles.filter { it.value == 1 }.joinToString(", ") { it.name }
        }

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

