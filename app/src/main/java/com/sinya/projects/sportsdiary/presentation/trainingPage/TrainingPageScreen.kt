package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomButton
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomDropdownMenu
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.ExerciseList
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory.TrainingBottomSheetCategory
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining.TrainingBottomSheetTraining

@Composable
fun TrainingPageScreen(
    state: TrainingPageUiState,
    onEvent: (TrainingPageUiEvent) -> Unit,
    onInfoClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    when (state) {
        is TrainingPageUiState.Loading -> PlaceholderScreen()
        is TrainingPageUiState.Success -> TrainingPage(
            id = state.id,
            title = "Тренировка №${state.title}",
            isOpenBottomSheetCategory = state.bottomSheetCategoryStatus,
            isOpenBottomSheetTraining = state.bottomSheetTrainingStatus,
            onInfoClick = onInfoClick,
            onBackClick = onBackClick,
            categories = state.categories,
            exercises = state.items,
            category = state.category,
            onEvent = onEvent
        )

        is TrainingPageUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun TrainingPage(
    id: Int?,
    onEvent: (TrainingPageUiEvent) -> Unit,
    title: String,
    category: TypeTraining,
    isOpenBottomSheetCategory: Boolean,
    isOpenBottomSheetTraining: Boolean,
    onInfoClick: () -> Unit,
    onBackClick: () -> Unit,
    categories: List<TypeTraining>,
    exercises: List<ExerciseItem>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = title,
            isVisibleBack = true,
            onBackClick = onBackClick,
            isVisibleSave = true,
            onSaveClick = { onEvent(TrainingPageUiEvent.Save(onBackClick)) }
        )
        CustomDropdownMenu(
            items = categories,
            title = stringResource(R.string.constructor),
            onInfoClick = onInfoClick,
            selectedItem = category,
            onOpenMenu = { onEvent(TrainingPageUiEvent.UpdateCategories) },
            onSelectedCategory = { name -> onEvent(TrainingPageUiEvent.OnSelectedCategory(name)) },
            onPlusClick = { onEvent(TrainingPageUiEvent.OpenBottomSheetCategory(true)) }
        )
        ExerciseList(
            exercises = exercises,
            onInfoClick = onInfoClick,
            onMinusClick = { id -> onEvent(TrainingPageUiEvent.Delete(id)) },
            onPlusClick = { id -> onEvent(TrainingPageUiEvent.AddSet(id)) },
            onDeleteSet = { id, index -> onEvent(TrainingPageUiEvent.DeleteSet(id, index)) },
            onEditSet = { id, index, value, valState ->
                onEvent(
                    TrainingPageUiEvent.EditSet(
                        id,
                        index,
                        value,
                        valState
                    )
                )
            },
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CustomButton(
                onClick = {
                    onEvent(TrainingPageUiEvent.OpenBottomSheetTraining(true))
                },
                text = stringResource(R.string.add_exercise)
            )
        }

        if (isOpenBottomSheetCategory) {
            TrainingBottomSheetCategory(
                onDismiss = {
                    onEvent(TrainingPageUiEvent.OpenBottomSheetCategory(false))
                }
            )
        }

        if (isOpenBottomSheetTraining) {
            TrainingBottomSheetTraining(
                id = id?:0,
                onDismiss = {
                    onEvent(TrainingPageUiEvent.OpenBottomSheetTraining(false))
                    onEvent(TrainingPageUiEvent.UpdateListTraining)
                }
            )
        }
    }
}

