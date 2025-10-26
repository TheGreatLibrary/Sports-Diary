package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote.ModalSheetNoteEvent
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomButton
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSheetPlan(
    onDismiss: () -> Unit,
    vm: ModalSheetPlanViewModel = hiltViewModel()
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
        when (val state = vm.state.value) {
            is ModalSheetPlanUiState.Loading -> PlaceholderScreen()
            is ModalSheetPlanUiState.Error -> ErrorScreen(state.message)
            is ModalSheetPlanUiState.Success -> ModalSheetPlanView(
                state = state,
                onEvent = vm::onEvent,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun ModalSheetPlanView(
    state: ModalSheetPlanUiState.Success,
    onEvent: (ModalSheetPlanEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val onClickBtn = {
        if (state.visibleAddField) onEvent(ModalSheetPlanEvent.AddPlan)
        else if (state.visibleEditFieldId != null) onEvent(ModalSheetPlanEvent.EditPlan(state.visibleEditFieldId))
        else onEvent(ModalSheetPlanEvent.OpenAddPlanField)
    }
    val textBtn = if (!state.visibleAddField) stringResource(R.string.create)
    else stringResource(R.string.save)

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.plans),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (state.items.isEmpty()) {
            Text(
                text = stringResource(R.string.nothing_found),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = it.description ?: stringResource(R.string.not_found_data),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 5.dp,
                                alignment = Alignment.End
                            )
                        ) {
                            AnimationIcon(
                                onClick = { onEvent(ModalSheetPlanEvent.OpenEditPlanField(it.id)) },
                                icon = painterResource(R.drawable.morn_edit),
                                description = "update",
                                isSelected = true,
                                size = 28.dp,
                                shape = MaterialTheme.shapes.small,
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                            AnimationIcon(
                                onClick = { onEvent(ModalSheetPlanEvent.DeletePlan(it.id)) },
                                icon = painterResource(R.drawable.morn_trash),
                                description = "delete",
                                isSelected = true,
                                size = 28.dp,
                                shape = MaterialTheme.shapes.small,
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                        }
                        AnimatedVisibility(state.visibleEditFieldId == it.id) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                CustomTextField(
                                    value = state.queryName,
                                    onValueChange = { s -> onEvent(ModalSheetPlanEvent.OnQueryNameChange(s)) },
                                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                                    keyboardType = KeyboardType.Text,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                CustomTextField(
                                    value = state.queryDescription,
                                    onValueChange = { s -> onEvent(ModalSheetPlanEvent.OnQueryDescriptionChange(s)) },
                                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                                    keyboardType = KeyboardType.Text,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
        AnimatedVisibility(state.visibleAddField) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CustomTextField(
                    value = state.queryName,
                    onValueChange = { s -> onEvent(ModalSheetPlanEvent.OnQueryNameChange(s)) },
                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth()
                )
                CustomTextField(
                    value = state.queryDescription,
                    onValueChange = { s -> onEvent(ModalSheetPlanEvent.OnQueryDescriptionChange(s)) },
                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth()
                )
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
                onClick = onClickBtn,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                text = textBtn
            )
        }
    }
}



