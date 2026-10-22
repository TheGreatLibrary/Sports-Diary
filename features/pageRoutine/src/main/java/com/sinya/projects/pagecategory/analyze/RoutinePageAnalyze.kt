package com.sinya.projects.pagecategory.analyze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.pagecategory.form.components.RoutinePagePlaceholder
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.map.BodyCard
import com.sinya.projects.ui.components.screen.ScreenLazyColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.BodyInfoSheetContent

@Composable
internal fun RoutinePageAnalyze(
    id: Int?,
    onBackClick: () -> Unit,
) {
    val viewModel: RoutinePageAnalyzeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadData(id)
        }
    }

    when (state) {
        RoutinePageAnalyzeUiState.Loading -> RoutinePagePlaceholder("", onBackClick)

        is RoutinePageAnalyzeUiState.Success -> {
            RoutineAnalyzeView(
                state = state as RoutinePageAnalyzeUiState.Success,
                onEvent = viewModel::onEvent,
                onBackClick = onBackClick,
            )
        }
    }
}

@Composable
private fun RoutineAnalyzeView(
    state: RoutinePageAnalyzeUiState.Success,
    onEvent: (RoutinePageAnalyzeEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
    ) {
        ScreenLazyColumn(
            arrangement = Arrangement.spacedBy(8.dp),
            navigationType = TypeTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = "Анализ рутины",
            )
        ) {
            item {
                BodyCard(
                    title = stringResource(R.string.effective_muscles),
                    list = state.dataExerciseMuscles.map {
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
                    onInfoClick = { onEvent(RoutinePageAnalyzeEvent.OnBodyInfoClick) }
                )
            }
        }

        if (state.bodyVisible) {
            CustomModalSheet(
                onDismiss = { onEvent(RoutinePageAnalyzeEvent.OnBodyInfoClick) },
                content = { BodyInfoSheetContent(listMuscles = state.allMuscles) }
            )
        }
    }
}