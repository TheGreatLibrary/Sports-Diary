package com.sinya.projects.pageExercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.pageExercise.components.CharacteristicRow
import com.sinya.projects.pageExercise.components.ExerciseMediaCarousel
import com.sinya.projects.pageExercise.components.ExercisePagePlaceholder
import com.sinya.projects.pageExercise.components.PointRule
import com.sinya.projects.ui.R.drawable
import com.sinya.projects.ui.R.string
import com.sinya.projects.ui.components.ExpandedCard
import com.sinya.projects.ui.components.map.BodyCard
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.BodyInfoSheetContent

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
            onBackClick = onBackClick
        )

        is ExercisePageUiState.Success -> {
            ExercisePageView(
                state = state as ExercisePageUiState.Success,
                onBackClick = onBackClick,
                onEvent = viewModel::onEvent,
                navigateToEdit = navigateToEdit
            )
        }

        is ExercisePageUiState.Error -> {

        }
    }
}

@Composable
private fun ExercisePageView(
    state: ExercisePageUiState.Success,
    onBackClick: () -> Unit,
    onEvent: (ExercisePageEvent) -> Unit,
    navigateToEdit: (Int) -> Unit
) {
    val navigationType = remember(state.exercise.isCustom, state.exercise.id) {
        if (state.exercise.isCustom) {
            TypeTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = "",
                painter = drawable.icon_edit,
                onClick = { navigateToEdit(state.exercise.id) }
            )
        } else {
            TypeTopNavigation.WithoutIcon(onBackClick = onBackClick, title = "")
        }
    }

    ScreenColumn(
        arrangement = Arrangement.spacedBy(12.dp),
        navigationType = navigationType
    ) {
        Text(
            text = state.exercise.name,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )

        ExpandedCard(
            title = stringResource(string.characteristics)
        ) {
            val notFoundText = stringResource(string.not_found_data)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CharacteristicRow(
                    label = stringResource(string.level),
                    value = state.exercise.level ?: notFoundText
                )
                CharacteristicRow(
                    label = stringResource(string.type),
                    value = state.exercise.force ?: notFoundText
                )
                CharacteristicRow(
                    label = stringResource(string.equipment),
                    value = state.exercise.equipment ?: notFoundText
                )
                CharacteristicRow(
                    label = stringResource(string.category),
                    value = state.exercise.category ?: notFoundText
                )
                CharacteristicRow(
                    label = stringResource(string.mechanic),
                    value = state.exercise.mechanic ?: notFoundText
                )
            }
        }

        ExerciseMediaCarousel(mediaItems = state.mediaItems)

        if (state.exercise.description.isNotBlank()) {
            ExpandedCard(title = stringResource(string.description)) {
                Text(
                    text = state.exercise.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (state.rules.isNotEmpty()) {
            ExpandedCard(title = stringResource(string.rules)) {
                state.rules.forEachIndexed { index, rule ->
                    PointRule(index + 1, rule)
                }
            }
        }

        BodyCard(
            title = stringResource(string.muscle_title),
            list = state.listMuscle,
            withText = false,
            onInfoClick = { onEvent(ExercisePageEvent.OnBodyInfoClick()) },
            withColorText = true
        )

        Spacer(Modifier.height(80.dp))
    }

    if (state.bodyVisible) {
        CustomModalSheet(
            onDismiss = { onEvent(ExercisePageEvent.OnBodyInfoClick(false)) },
            content = { BodyInfoSheetContent(listMuscles = state.allMuscles) }
        )
    }
}