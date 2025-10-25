package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSheetPlan(
    onDismiss: () -> Unit,
    viewModel: ModalSheetPlanViewModel = hiltViewModel()
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
        when (val state = viewModel.state.value) {
            is ModalSheetPlanUiState.Loading -> PlaceholderScreen()
            is ModalSheetPlanUiState.Error -> ErrorScreen(state.message)
            is ModalSheetPlanUiState.Success -> {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.items.isEmpty()) {
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
                            items(state.items) { ex ->
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = ex.name
                                    )
                                    Text(
                                        text = ex.description?:"Заметки нет"
                                    )
                                }
                                HorizontalDivider()
                            }
                        }
                    }
//
//                    Row(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        TextButton(
//                            onClick = onDismiss,
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color.Transparent,
//                                disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer,
//                                contentColor = MaterialTheme.colorScheme.secondary
//                            )
//                        ) {
//                            Text(
//                                stringResource(R.string.cancel),
//                                style = MaterialTheme.typography.bodyMedium,
//                            )
//                        }
//                        Spacer(Modifier.width(8.dp))
//                        CustomButton(
//                            text = stringResource(R.string.create),
//                            containerColor = MaterialTheme.colorScheme.primary,
//                            contentColor = MaterialTheme.colorScheme.onPrimary,
//                            onClick = {
//                                onEvent(
//                                    TrainingBottomSheetTrainingUiEvent.AddTrainings(
//                                        onDone = onDismiss,
//                                        onError = { }
//                                    )
//                                )
//                            },
//                        )
//                    }
                }
            }
        }
    }
}




