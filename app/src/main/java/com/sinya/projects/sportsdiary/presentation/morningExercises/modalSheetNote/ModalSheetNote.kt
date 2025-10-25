package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetNote

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
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomButton
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSheetNote(
    onDismiss: () -> Unit,
    vm: ModalSheetNoteViewModel = hiltViewModel()
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
            is ModalSheetNoteUiState.Loading -> PlaceholderScreen()
            is ModalSheetNoteUiState.Error -> ErrorScreen(state.message)
            is ModalSheetNoteUiState.Success -> {
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
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.items) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = it.note ?: "Нет заметки",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = it.date,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                        AnimationIcon(
                                            onClick = {},
                                            icon = painterResource(R.drawable.plus),
                                            description = "update",
                                            isSelected = true
                                        )
                                        AnimationIcon(
                                            onClick = {},
                                            icon = painterResource(R.drawable.nav_set),
                                            description = "delete",
                                            isSelected = true
                                        )
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
//
                    }

                    if (state.visibleAddField) {
                        CustomTextField(
                            value = state.query,
                            onValueChange = { s -> vm.onEvent(ModalSheetNoteEvent.OnQueryChange(s)) },
                            onTrailingClick = {},
                            keyboardType = KeyboardType.Text,
                            modifier = Modifier.fillMaxWidth()
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
                            onClick = {
                                if (state.visibleAddField) vm.onEvent(ModalSheetNoteEvent.AddNote)
                                else vm.onEvent(ModalSheetNoteEvent.OpenAddNoteField) },
                            containerColor = if (state.items.firstOrNull() {
                                    it.date == LocalDate.now().toString()
                                } == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (state.items.firstOrNull() {
                                    it.date == LocalDate.now().toString()
                                } == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                            text = if (state.items.firstOrNull() {
                                    it.date == LocalDate.now().toString()
                                } == null)
                                stringResource(R.string.create)
                            else
                                stringResource(R.string.morning_has_note)
                        )
                    }
                }
            }
        }
    }
}




