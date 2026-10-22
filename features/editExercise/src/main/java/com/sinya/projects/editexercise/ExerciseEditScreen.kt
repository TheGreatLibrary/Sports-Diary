package com.sinya.projects.editexercise

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.enums.ExerciseMode
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.editexercise.components.ExerciseEditPlaceholder
import com.sinya.projects.editexercise.components.MuscleSheetContent
import com.sinya.projects.editexercise.subScreen.CharacteristicsScreen
import com.sinya.projects.editexercise.subScreen.DescriptionScreen
import com.sinya.projects.editexercise.subScreen.InstructionScreen
import com.sinya.projects.editexercise.subScreen.MuscleScreen
import com.sinya.projects.ui.components.PagerDots
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.trainingConstructor.UndoBottomNavigation
import com.sinya.projects.ui.components.CustomSnackbar
import com.sinya.projects.ui.components.dialog.GuideDescriptionView
import com.sinya.projects.ui.components.dialog.GuideDialog
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

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
            title = stringResource(com.sinya.projects.ui.R.string.create_exercise_title)
        )

        is ExerciseEditUiState.ExerciseForm -> {
            ExerciseEditView(
                state = state as ExerciseEditUiState.ExerciseForm,
                onEvent = viewModel::onEvent,
                onBackClick = onBackClick
            )
        }

        is ExerciseEditUiState.Error -> {

        }

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
    val stateBottomSheet = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val list = remember(state.muscles) {
        state.muscles.filter { it.checked }.sortedByDescending { it.value }
    }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 4 }
    )

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = context.getString(message),
                duration = SnackbarDuration.Short
            )
            onEvent(ExerciseEditEvent.ShowError(null))
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenColumn(
            modifier = Modifier.fillMaxSize(),
            arrangement = Arrangement.spacedBy(8.dp),
            scrollable = false,
            navigationType = TypeTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = stringResource(com.sinya.projects.ui.R.string.create_exercise_title),
                painter = com.sinya.projects.ui.R.drawable.nav_save,
                onClick = { onEvent(ExerciseEditEvent.Save) }
            )
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxHeight(0.95f),
                state = pagerState,
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> DescriptionScreen(
                        modifier = Modifier,
                        name = state.exercise.name,
                        namePlaceholder = stringResource(com.sinya.projects.ui.R.string.put_your_title),
                        isNameError = state.exercise.isError,
                        descriptionPlaceholder = stringResource(com.sinya.projects.ui.R.string.put_your_description),
                        onNameChange = { s ->
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.OnTitleChange(state.exercise.name, s)
                                )
                            )
                        },
                        description = state.exercise.description,
                        onDescriptionChange = { s ->
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.OnDescriptionChange(
                                        state.exercise.description,
                                        s
                                    )
                                )
                            )
                        },
                        imageUri = state.exercise.icon?.toUri(),
                        onImageSelected = { uri ->
                            onEvent(ExerciseEditEvent.OnImagePicked(uri, context))
                        }
                    )

                    1 -> CharacteristicsScreen(
                        state = state,
                        onEvent = onEvent
                    )

                    2 -> InstructionScreen(
                        rule = state.exercise.rule,
                        onRuleChange = { s ->
                            onEvent(
                                ExerciseEditEvent.Undoable(
                                    ExerciseUndoableAction.OnRuleChange(state.exercise.rule, s)
                                )
                            )
                        }
                    )

                    3 -> MuscleScreen(
                        list = list,
                        onEvent = onEvent,
                        openMenu = { stateBottomSheet.value = !stateBottomSheet.value }
                    )
                }
            }
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                PagerDots(
                    currentPage = pagerState.currentPage,
                    count = pagerState.pageCount,
                    size = 5.dp
                )
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
                .padding(bottom = 40.dp, start = 16.dp, end = 16.dp),
            onUndoClick = { onEvent(ExerciseEditEvent.Undo) },
            canUndo = state.canUndo,
            isUndoing = state.isUndoing,
            onRedoClick = { onEvent(ExerciseEditEvent.Redo) },
            canRedo = state.canRedo,
        )

        CustomSnackbar(snackbarHostState)


        if (stateBottomSheet.value) {
            CustomModalSheet(
                modifier = Modifier,
                onDismiss = { stateBottomSheet.value = !stateBottomSheet.value }
            ) {
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
                    onClickSuccess = { stateBottomSheet.value = !stateBottomSheet.value },
                    onDismiss = { stateBottomSheet.value = !stateBottomSheet.value }
                )
            }
        }
    }
}