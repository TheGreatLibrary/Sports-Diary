package com.sinya.projects.sportsdiary.presentation.exerciseEdit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.enums.ExerciseMode
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.components.ExerciseDropdowns
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.components.ExerciseEditPlaceholder
import com.sinya.projects.sportsdiary.sheets.MuscleSheetContent
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomSnackbar
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.ScreenLazyColumn
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.UndoBottomNavigation
import kotlinx.coroutines.launch

@Composable
fun ExerciseEditScreen(
    id: Int?,
    onBackClick: () -> Unit,
    navigateToExercisePage: (Int) -> Unit
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: ExerciseEditViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ExerciseEditUiState.Loading -> ExerciseEditPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(R.string.create_exercise_title)
        )

        is ExerciseEditUiState.ExerciseForm -> {
            ExerciseEditView(
                state = state as ExerciseEditUiState.ExerciseForm,
                onEvent = viewModel::onEvent,
                onBackClick = onBackClick
            )
        }

        is ExerciseEditUiState.Error -> ErrorScreen(
            onBackClick,
            (state as ExerciseEditUiState.Error).message
        )

        is ExerciseEditUiState.Success -> {
            LaunchedEffect(state as ExerciseEditUiState.Success) {
                navigateToExercisePage((state as ExerciseEditUiState.Success).id)
            }
        }
    }
}


@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseEditView(
    state: ExerciseEditUiState.ExerciseForm,
    onEvent: (ExerciseEditEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val list = remember(state.muscles) {
        state.muscles.filter { it.checked }.sortedByDescending { it.value }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = context.getString(message),
                duration = SnackbarDuration.Short
            )
            onEvent(ExerciseEditEvent.ShowError(null))
        }
    }


    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = {
            MuscleSheetContent(
                list = state.muscles,
                onToggle = { id, checked ->
                    onEvent(
                        ExerciseEditEvent.Undoable(
                            ExerciseUndoableAction.ToggleMuscleValue(
                                id,
                                if (!checked) ExerciseMode.NONE else ExerciseMode.MAIN,
                                if (!checked) ExerciseMode.MAIN else ExerciseMode.NONE
                            )
                        )
                    )
                },
                onRadioToggle = { id, oldValue, value ->
                    onEvent(
                        ExerciseEditEvent.Undoable(
                            ExerciseUndoableAction.ToggleMuscleValue(
                                id,
                                oldValue,
                                value
                            )
                        )
                    )
                },
                onClickSuccess = { /*onEvent(ExercisePageEvent)*/ },
                scaffoldState = scaffoldState
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenLazyColumn(
                arrangement = Arrangement.spacedBy(8.dp),
                navigationType = TypeTopNavigation.WithIcon(
                    onBackClick = onBackClick,
                    title = stringResource(R.string.create_exercise_title),
                    painter = R.drawable.nav_save,
                    onClick = { onEvent(ExerciseEditEvent.Save) }
                )
            ) {
                item {
                    CustomTextField(
                        value = state.exercise.name,
                        onValueChange = { s ->
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.OnTitleChange(state.exercise.name, s)
                                )
                            )
                        },
                        onTrailingClick = {
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.OnTitleChange(state.exercise.name, "")
                                )
                            )
                        },
                        placeholder = stringResource(R.string.put_your_title),
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        isError = state.exercise.isError,
                        errorMessage = stringResource(R.string.exercise_name_is_empty),
                        shape = MaterialTheme.shapes.extraLarge,
                        focusRequester = state.focusRequesters[0]
                    )
                }

                item {
                    CustomTextField(
                        value = state.exercise.description,
                        onValueChange = { s ->
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.OnDescriptionChange(
                                        state.exercise.description,
                                        s
                                    )
                                )
                            )
                        },
                        placeholder = stringResource(R.string.put_your_description),
                        height = 160.dp,
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        singleLine = false,
                        focusRequester = state.focusRequesters[1]
                    )
                }

                item {
                    CustomTextField(
                        value = state.exercise.rule,
                        onValueChange = { s ->
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.OnRuleChange(state.exercise.rule, s)
                                )
                            )
                        },
                        placeholder = stringResource(R.string.put_your_rule),
                        singleLine = false,
                        height = 160.dp,
                        modifier = Modifier.fillMaxWidth(),
                        imeAction = ImeAction.Done,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusRequester = state.focusRequesters[2]
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
                            text = stringResource(R.string.not_found_data),
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
                        description = when (ExerciseMode.getMode(item.value)) {
                            ExerciseMode.MAIN -> stringResource(R.string.primary_muscl)
                            else -> stringResource(R.string.secondary_muscl)
                        },
                        onTrainingClick = { },
                        onDelete = {
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.ToggleMuscleValue(
                                        item.muscleId,
                                        ExerciseMode.getMode(item.value),
                                        ExerciseMode.NONE
                                    )
                                )
                            )
                        },
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
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            text = stringResource(R.string.add_exercise),
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(180.dp))
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

            UndoBottomNavigation(
                modifier = Modifier
                    .padding(bottom = 70.dp, start = 16.dp, end = 16.dp),
                onUndoClick = { onEvent(ExerciseEditEvent.Undo) },
                canUndo = state.canUndo,
                isUndoing = state.isUndoing,
                onRedoClick = { onEvent(ExerciseEditEvent.Redo) },
                canRedo = state.canRedo,
            )

            CustomSnackbar(snackbarHostState)
        }
    }
}