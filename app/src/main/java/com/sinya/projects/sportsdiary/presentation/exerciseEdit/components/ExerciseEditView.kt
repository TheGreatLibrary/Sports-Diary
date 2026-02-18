package com.sinya.projects.sportsdiary.presentation.exerciseEdit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.ExerciseEditEvent
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.ExerciseEditUiState
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditView(
    state: ExerciseEditUiState.ExerciseForm,
    onEvent: (ExerciseEditEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val list = remember(state.muscles) {
        state.muscles.filter { it.checked }.sortedByDescending { it.value }
    }

    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = {
            MuscleSheetContent(
                list = state.muscles,
                onToggle = { id -> onEvent(ExerciseEditEvent.MuscleToggle(id)) },
                onRadioToggle = { id, value -> onEvent(ExerciseEditEvent.MuscleToggleValue(id, value)) },
                onClickSuccess = { /*onEvent(ExercisePageEvent)*/ },
                scaffoldState = scaffoldState
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                NavigationTopBar(
                    type = TypeAppTopNavigation.WithIcon(
                        onBackClick = onBackClick,
                        title = stringResource(R.string.create_exercise),
                        painter = R.drawable.nav_save,
                        onClick = { onEvent(ExerciseEditEvent.Save) }
                    )
                )
            }

            item {
                CustomTextField(
                    value = state.exercise.name,
                    onValueChange = { s -> onEvent(ExerciseEditEvent.OnNameChange(s)) },
                    onTrailingClick = { onEvent(ExerciseEditEvent.OnNameChange("")) },
                    placeholder = stringResource(R.string.put_your_title),
                    modifier = Modifier.fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    isError = state.exercise.isError,
                    errorMessage = stringResource(R.string.category_name_is_empty),
                    shape = MaterialTheme.shapes.extraLarge
                )
            }

            item {
                CustomTextField(
                    value = state.exercise.description,
                    onValueChange = { s -> onEvent(ExerciseEditEvent.OnDescriptionChange(s)) },
                    placeholder = stringResource(R.string.put_your_description),
                    height = 160.dp,
                    modifier = Modifier.fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    singleLine = false,
                )
            }

            item {
                CustomTextField(
                    value = state.exercise.rule,
                    onValueChange = { s -> onEvent(ExerciseEditEvent.OnRuleChange(s)) },
                    placeholder = stringResource(R.string.put_your_rule),
                    singleLine = false,
                    height = 160.dp,
                    modifier = Modifier.fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            }

            item {
                ExerciseDropdowns(state = state, onEvent = onEvent)
            }

            item {
                Text(
                    text = stringResource(R.string.muscle_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                if (!state.muscles.any { it.checked }) {
                    Text(
                        text = stringResource(R.string.nothing_found),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary

                    )
                }
            }

            items(
                items = list,
                key = { it.muscleId }
            ) { item ->
                SwipeCard(
                    modifier = Modifier,
                    id = item.muscleId,
                    title = item.name,
                    description = when(item.value) {
                        2 -> stringResource(R.string.primary_muscl)
                        else -> stringResource(R.string.secondary_muscl)
                    },
                    onTrainingClick = { },
                    onDelete = { onEvent(ExerciseEditEvent.DeleteMuscle(item.muscleId)) },
                )
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CustomButton(
                        onClick = {
                            scope.launch {
                                onEvent(ExerciseEditEvent.OpenBottomSheetTraining)
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        text = stringResource(R.string.add_exercise),
                    )
                }
                Spacer(Modifier.height(80.dp))
            }

            item {
                Spacer(Modifier.height(100.dp))
            }
        }

        state.dialogContent?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(ExerciseEditEvent.DialogShown)
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
    }
}