package com.sinya.projects.pageExercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.pageExercise.components.CharacteristicRow
import com.sinya.projects.pageExercise.components.ExercisePagePlaceholder
import com.sinya.projects.pageExercise.components.PointRule
import com.sinya.projects.ui.R.drawable
import com.sinya.projects.ui.R.string
import com.sinya.projects.ui.components.map.BodyCard
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.BodyInfoSheetContent
import com.sinya.projects.ui.components.ExpandedCard

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
    val rules by remember(state.exercise.rule) {
        mutableStateOf(state.exercise.rule.split("\n\n"))
    }
    val icon = remember(state.exercise.icon) { state.exercise.icon?.toUri() }
    val listMuscle = remember(state.exMuscles) {
        state.exMuscles.map {
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
        }
    }

    ScreenColumn(
        arrangement = Arrangement.spacedBy(12.dp),
        navigationType = when (state.exercise.isCustom) {
            true -> TypeTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = "",
                painter = drawable.icon_edit,
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
            title = stringResource(string.characteristics)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CharacteristicRow(
                    label = stringResource(string.level),
                    value = state.exercise.level ?: stringResource(string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(string.type),
                    value = state.exercise.force ?: stringResource(string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(string.equipment),
                    value = state.exercise.equipment ?: stringResource(string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(string.category),
                    value = state.exercise.category ?: stringResource(string.not_found_data)
                )
                CharacteristicRow(
                    label = stringResource(string.mechanic),
                    value = state.exercise.mechanic ?: stringResource(string.not_found_data)
                )
            }
        }

        if(icon == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 360.dp)
                    .padding(bottom = 10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = icon,
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small)
                )
            }
        }

        ExpandedCard(
            title = stringResource(string.description)
        ) {
            Text(
                text = state.exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        ExpandedCard(
            title = stringResource(string.rules),
        ) {
            rules.forEachIndexed { index, it -> PointRule(index + 1, it) }
        }

        BodyCard(
            title = stringResource(string.muscle_title),
            list = listMuscle,
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


