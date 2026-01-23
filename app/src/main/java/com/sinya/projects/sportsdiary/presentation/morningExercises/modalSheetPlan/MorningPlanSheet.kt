package com.sinya.projects.sportsdiary.presentation.morningExercises.modalSheetPlan

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomTextFieldWithLabel
import com.sinya.projects.sportsdiary.utils.getString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorningPlanSheet(
    currentPlanId: Int?,
    onPlanClick: (Int?) -> Unit,
    onDismiss: () -> Unit,
    vm: MorningPlanViewModel = hiltViewModel()
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
            is MorningPlanUiState.Loading -> PlaceholderScreen()
            is MorningPlanUiState.Error -> ErrorScreen(state.message)
            is MorningPlanUiState.Success -> MorningPlanView(
                state = state,
                onEvent = vm::onEvent,
                onDismiss = onDismiss,
                currentPlanId = currentPlanId,
                onPlanClick = onPlanClick,
            )
        }
    }
}

@Composable
private fun MorningPlanView(
    currentPlanId: Int?,
    onPlanClick: (Int?) -> Unit,
    state: MorningPlanUiState.Success,
    onEvent: (ModalSheetPlanEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val onClickBtn = {
        if (state.visibleAddField) onEvent(ModalSheetPlanEvent.AddPlan)
        else if (state.visibleEditFieldId != null) onEvent(ModalSheetPlanEvent.EditPlan(state.visibleEditFieldId))
        else onEvent(ModalSheetPlanEvent.OpenAddPlanField)
    }
    val textBtn = if (state.visibleEditFieldId == null) stringResource(R.string.create)
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
                    .fillMaxHeight(0.3f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row {
                            RadioButton(
                                selected = it?.id == currentPlanId,
                                onClick = { onPlanClick(it?.id) }
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = it?.name ?: stringResource(R.string.base_plan),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    )
                                    it?.let {
                                        AnimationIcon(
                                            onClick = {
                                                onEvent(
                                                    ModalSheetPlanEvent.OpenEditPlanField(
                                                        it.id
                                                    )
                                                )
                                            },
                                            icon = painterResource(R.drawable.morn_edit),
                                            description = "update",
                                            isSelected = true,
                                            size = 28.dp,
                                            shape = MaterialTheme.shapes.small,
                                            selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                            selectedContentColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = it?.description ?: stringResource(R.string.base_description),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    )
                                    it?.let {
                                        AnimationIcon(
                                            onClick = {
                                                onEvent(ModalSheetPlanEvent.DeletePlan(it.id))
                                                onPlanClick(null)
                                            },
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
                            }
                        }

                        AnimatedVisibility(it!=null && state.visibleEditFieldId == it.id) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                CustomTextFieldWithLabel(
                                    label = stringResource(R.string.name),
                                    value = state.queryName,
                                    onValueChange = { s ->
                                        onEvent(
                                            ModalSheetPlanEvent.OnQueryNameChange(
                                                s
                                            )
                                        )
                                    },
                                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                                    keyboardType = KeyboardType.Text,
                                    modifier = Modifier.fillMaxWidth(),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                                CustomTextFieldWithLabel(
                                    label = stringResource(R.string.description),
                                    value = state.queryDescription,
                                    onValueChange = { s ->
                                        onEvent(
                                            ModalSheetPlanEvent.OnQueryDescriptionChange(
                                                s
                                            )
                                        )
                                    },
                                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                                    keyboardType = KeyboardType.Text,
                                    modifier = Modifier.fillMaxWidth(),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = MaterialTheme.colorScheme.onSurface
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
                CustomTextFieldWithLabel(
                    label = stringResource(R.string.name),
                    value = state.queryName,
                    onValueChange = { s ->
                        onEvent(
                            ModalSheetPlanEvent.OnQueryNameChange(
                                s
                            )
                        )
                    },
                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
                CustomTextFieldWithLabel(
                    label = stringResource(R.string.description),
                    value = state.queryDescription,
                    onValueChange = { s ->
                        onEvent(
                            ModalSheetPlanEvent.OnQueryDescriptionChange(
                                s
                            )
                        )
                    },
                    onTrailingClick = { onEvent(ModalSheetPlanEvent.ClearQuery) },
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
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
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                text = textBtn
            )
        }
    }
}



