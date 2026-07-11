package com.sinya.projects.sportsdiary.presentation.exercisePage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.components.CharacteristicRow
import com.sinya.projects.sportsdiary.presentation.exercisePage.components.ExercisePagePlaceholder
import com.sinya.projects.sportsdiary.presentation.exercisePage.components.PointRule
import com.sinya.projects.sportsdiary.sheets.BodyInfoSheetContent
import com.sinya.projects.sportsdiary.ui.features.ExpandedCard
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.map.BodyCard
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscles

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
        ExercisePageUiState.Loading -> ExercisePagePlaceholder(
            onBackClick = onBackClick,
            title = ""
        )

        is ExercisePageUiState.Success -> {
            ExercisePageView(
                state = state as ExercisePageUiState.Success,
                onBackClick = onBackClick,
                onEvent = viewModel::onEvent,
                navigateToEdit = navigateToEdit
            )
        }

        is ExercisePageUiState.Error -> ErrorScreen(
            onBackClick,
            (state as ExercisePageUiState.Error).message
        )
    }
}

@Composable
private fun ExercisePageView(
    state: ExercisePageUiState.Success,
    onBackClick: () -> Unit,
    onEvent: (ExercisePageEvent) -> Unit,
    navigateToEdit: (Int) -> Unit
) {
    val rules by remember(state.exercise.rule) {
        mutableStateOf(state.exercise.rule.split("\n\n"))
    }

    ScreenColumn(
        arrangement = Arrangement.spacedBy(12.dp),
        navigationType = when(state.exercise.isCustom) {
            true -> TypeTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = "",
                painter = R.drawable.icon_edit,
                onClick = { navigateToEdit(state.exercise.id) }
            )
            false -> TypeTopNavigation.WithoutIcon(onBackClick = onBackClick, title = "")
        }
    ) {
        Text(
            text = state.exercise.name,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )

        ExpandedCard(
            title = stringResource(R.string.characteristics)
        ) {
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

        ExpandedCard(
            title = stringResource(R.string.description)
        ) {
            Text(
                text = state.exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        ExpandedCard(
            title = stringResource(R.string.rules),
        ) {
            rules.forEachIndexed { index, it -> PointRule(index + 1, it) }
        }

        BodyCard(
            title = stringResource(R.string.muscle_title),
            list = state.exMuscles.map {
                DataExerciseMuscles(
                    muscleId = it.muscleId,
                    name = it.name,
                    value = 1f,
                    level = it.value,
                    centerX = 0f,
                    centerY = 0f,
                    radiusScale = 0f,
                    res = 0
                )
            },
            withText = false,
            onInfoClick = { onEvent(ExercisePageEvent.OnBodyInfoClick()) },
            withColorText = true
        )

        Spacer(Modifier.height(80.dp))
    }

    if (state.bodyVisible) {
        BodyInfoSheetContent(
            onDismiss = { onEvent(ExercisePageEvent.OnBodyInfoClick(false)) },
            listMuscles = state.allMuscles
        )
    }
}


