package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

import androidx.compose.animation.AnimatedVisibility
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
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorningNoteSheet(
    onDismiss: () -> Unit,
    vm: MorningNoteViewModel = hiltViewModel()
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
        when (val state = vm.state.value) {
            is MorningNoteUiState.Loading -> PlaceholderScreen()
            is MorningNoteUiState.Error -> ErrorScreen(state.message)
            is MorningNoteUiState.Success -> MorningNoteView(
                state = state,
                onEvent = vm::onEvent,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun MorningNoteView(
    state: MorningNoteUiState.Success,
    onEvent: (ModalSheetNoteEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val hasData = state.items.firstOrNull { it.date == LocalDate.now().toString() } == null
    val onClickBtn = {
        if (state.visibleAddField) onEvent(ModalSheetNoteEvent.AddNote)
        else if (state.visibleEditFieldId != null) onEvent(ModalSheetNoteEvent.EditNote(state.visibleEditFieldId))
        else onEvent(ModalSheetNoteEvent.OpenAddNoteField)
    }
    val textBtn = if (hasData) stringResource(R.string.create)
    else if (state.visibleEditFieldId != null) stringResource(R.string.save)
    else stringResource(R.string.morning_has_note)

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.notes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (state.items.isEmpty()) {
            Text(
                text = stringResource(R.string.nothing_found),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = it.note ?: stringResource(R.string.not_found_data),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = it.date,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                AnimationIcon(
                                    onClick = { onEvent(ModalSheetNoteEvent.OpenEditNoteField(it.id)) },
                                    icon = painterResource(R.drawable.morn_edit),
                                    description = "update",
                                    isSelected = true,
                                    size = 28.dp,
                                    shape = MaterialTheme.shapes.small,
                                    selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    selectedContentColor = MaterialTheme.colorScheme.onSurface
                                )
                                AnimationIcon(
                                    onClick = { onEvent(ModalSheetNoteEvent.ClearNote(it.id)) },
                                    icon = painterResource(R.drawable.morn_trash),
                                    description = "delete",
                                    isSelected = true,
                                    size = 28.dp,
                                    shape = MaterialTheme.shapes.small,
                                    selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    selectedContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        AnimatedVisibility(state.visibleEditFieldId == it.id) {
                            CustomTextField(
                                value = state.query,
                                onValueChange = { s -> onEvent(ModalSheetNoteEvent.OnQueryChange(s)) },
                                onTrailingClick = { onEvent(ModalSheetNoteEvent.ClearQuery) },
                                keyboardType = KeyboardType.Text,
                                modifier = Modifier.fillMaxWidth(),
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

        AnimatedVisibility(state.visibleAddField) {
            CustomTextField(
                value = state.query,
                onValueChange = { s -> onEvent(ModalSheetNoteEvent.OnQueryChange(s)) },
                onTrailingClick = { onEvent(ModalSheetNoteEvent.ClearQuery) },
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
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
                containerColor = if (hasData || state.visibleEditFieldId != null) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (hasData || state.visibleEditFieldId != null) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSecondary,
                text = textBtn
            )
        }
    }
}



