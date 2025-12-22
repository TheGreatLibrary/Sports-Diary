package com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetExercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CustomElementCheckBox

@Composable
fun TrainingExerciseSheet(
    id: Int,
    onDismiss: () -> Unit,
    viewModel: TrainingExerciseViewModel = hiltViewModel()
) {
    LaunchedEffect(id) { viewModel.init(id) }

    TrainingExerciseView(
        state = viewModel.state.value,
        onEvent = viewModel::onEvent,
        onDismiss = onDismiss,
        triState = viewModel.triState(),
        filtered = viewModel.filtered(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingExerciseView(
    state: TrainingExerciseUiState,
    onEvent: (TrainingExerciseEvent) -> Unit,
    triState: ToggleableState,
    filtered: List<ExerciseUi>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.large,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        windowInsets = BottomSheetDefaults.windowInsets,
    ) {
        when (state) {
            is TrainingExerciseUiState.Loading -> PlaceholderScreen()
            is TrainingExerciseUiState.Error -> ErrorScreen(state.message)
            is TrainingExerciseUiState.Success -> {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.choose_exercises),
                        style = MaterialTheme.typography.titleMedium
                    )
                    CustomTextField(
                        value = state.query,
                        onValueChange = { s ->
                            onEvent(
                                TrainingExerciseEvent.OnQueryChange(
                                    s
                                )
                            )
                        },
                        onTrailingClick = {
                            onEvent(
                                TrainingExerciseEvent.OnQueryChange(
                                    ""
                                )
                            )
                        },
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
//                    CustomElementTriCheck(
//                        triState = triState,
//                        onEvent = {
//                            val target = triState != ToggleableState.On
//                            onEvent(TrainingBottomSheetTrainingUiEvent.SetAll(target))
//                        })

                    HorizontalDivider(color = MaterialTheme.colorScheme.secondaryContainer)
                    if (filtered.isEmpty()) {
                        Text(
                            text = stringResource(R.string.nothing_found),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.45f)
                        ) {
                            items(filtered) { ex ->
                                CustomElementCheckBox(
                                    onEvent = {
                                        onEvent(
                                            TrainingExerciseEvent.Toggle(
                                                ex.id
                                            )
                                        )
                                    },
                                    ex = ex
                                )
                            }
                        }
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(
                                stringResource(R.string.cancel),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        CustomButton(
                            text = stringResource(R.string.create),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = {
                                onEvent(TrainingExerciseEvent.AddTrainings(
                                    onDone = onDismiss,
                                    onError = { }
                                )
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}



