package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.TrainingCategorySheet
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises.TrainingExerciseSheet
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog

@Composable
fun TrainingPageScreen(
    state: TrainingPageUiState,
    onEvent: (TrainingPageEvent) -> Unit,
    onInfoClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    when (state) {
        is TrainingPageUiState.Loading -> PlaceholderScreen()
        is TrainingPageUiState.Success -> TrainingPage(
            id = state.id,
            dialogContent = state.dialogContent,
            onEvent = onEvent,
            title = stringResource(R.string.training_number, state.title),
            category = state.category,
            isOpenBottomSheetCategory = state.bottomSheetCategoryStatus,
            isOpenBottomSheetTraining = state.bottomSheetTrainingStatus,
            onInfoClick = onInfoClick,
            onBackClick = onBackClick,
            categories = state.categories,
            exercises = state.items,
        )

        is TrainingPageUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun TrainingPage(
    id: Int?,
    dialogContent: ExerciseDialogContent?,
    onEvent: (TrainingPageEvent) -> Unit,
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
            onSecondaryClick = { onEvent(TrainingPageEvent.Save(onBackClick)) }
        )
        CustomDropdownMenu(
            items = categories,
            title = stringResource(R.string.constructor),
            onInfoClick = onInfoClick,
            selectedItem = category,
            onOpenMenu = { onEvent(TrainingPageEvent.UpdateCategories) },
            onSelectedCategory = { name -> onEvent(TrainingPageEvent.OnSelectedCategory(name)) },
            onPlusClick = { onEvent(TrainingPageEvent.OpenBottomSheetCategory(true)) }
        )
        ExerciseList(
            exercises = exercises,
            onInfoClick = { id -> onEvent(TrainingPageEvent.OpenDialog(id)) },
            onMinusClick = { id -> onEvent(TrainingPageEvent.Delete(id)) },
            onPlusClick = { id -> onEvent(TrainingPageEvent.AddSet(id)) },
            onDeleteSet = { id, index -> onEvent(TrainingPageEvent.DeleteSet(id, index)) },
            onEditSet = { id, index, value, valState ->
                onEvent(
                    TrainingPageEvent.EditSet(
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
                    onEvent(TrainingPageEvent.OpenBottomSheetTraining(true))
                },
                text = stringResource(R.string.add_exercise),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        dialogContent?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(TrainingPageEvent.OpenDialog(null))
                },
                content = {
                    GuideDescriptionView(
                        title = it.name,
                        description = it.description,
                        image = null
                    )
                }
            )
        }

        if (isOpenBottomSheetCategory) {
            TrainingCategorySheet(
                onDismiss = {
                    onEvent(TrainingPageEvent.OpenBottomSheetCategory(false))
                }
            )
        }

        if (isOpenBottomSheetTraining) {
            TrainingExerciseSheet(
                id = id?:0,
                onDismiss = {
                    onEvent(TrainingPageEvent.UpdateListTraining)
                }
            )
        }
    }
}

