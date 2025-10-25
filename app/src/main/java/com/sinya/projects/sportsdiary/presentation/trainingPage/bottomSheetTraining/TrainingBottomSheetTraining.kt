package com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetTraining

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CustomElementCheckBox
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CustomElementTriCheck

@Composable
fun TrainingBottomSheetTraining(
    id: Int,
    onDismiss: () -> Unit,
    viewModel: TrainingBottomSheetTrainingViewModel = hiltViewModel()
) {
    LaunchedEffect(id) { viewModel.init(id) }

    TrainingBottomSheetTrainingView(
        state = viewModel.state.value,
        onEvent = viewModel::onEvent,
        onDismiss = onDismiss,
        triState = viewModel.triState(),
        filtered = viewModel.filtered(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingBottomSheetTrainingView(
    state: TrainingBottomSheetTrainingUiState,
    onEvent: (TrainingBottomSheetTrainingUiEvent) -> Unit,
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
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        windowInsets = BottomSheetDefaults.windowInsets,
    ) {
        when (state) {
            is TrainingBottomSheetTrainingUiState.Loading -> PlaceholderScreen()
            is TrainingBottomSheetTrainingUiState.Error -> ErrorScreen(state.message)
            is TrainingBottomSheetTrainingUiState.Success -> {
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
                                TrainingBottomSheetTrainingUiEvent.OnQueryChange(
                                    s
                                )
                            )
                        },
                        onTrailingClick = {
                            onEvent(
                                TrainingBottomSheetTrainingUiEvent.OnQueryChange(
                                    ""
                                )
                            )
                        },
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth()
                    )
//                    CustomElementTriCheck(
//                        triState = triState,
//                        onEvent = {
//                            val target = triState != ToggleableState.On
//                            onEvent(TrainingBottomSheetTrainingUiEvent.SetAll(target))
//                        })

                    HorizontalDivider()
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
                                .fillMaxHeight(0.7f)
                        ) {
                            items(filtered) { ex ->
                                CustomElementCheckBox(
                                    onEvent = {
                                        onEvent(
                                            TrainingBottomSheetTrainingUiEvent.Toggle(
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
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            onClick = {
                                onEvent(TrainingBottomSheetTrainingUiEvent.AddTrainings(
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



