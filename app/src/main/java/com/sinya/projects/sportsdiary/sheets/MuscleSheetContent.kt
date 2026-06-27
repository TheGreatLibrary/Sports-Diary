package com.sinya.projects.sportsdiary.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseMusclesData
import com.sinya.projects.sportsdiary.core.domain.model.RadioItem
import com.sinya.projects.sportsdiary.core.domain.enums.ExerciseMode
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.components.MuscleWithRadioButton
import com.sinya.projects.sportsdiary.ui.features.ModalSheetButtons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleSheetContent(
    onClickSuccess: () -> Unit,
    list: List<ExerciseMusclesData>,
    onToggle: (Int, Boolean) -> Unit,
    onRadioToggle: (Int, ExerciseMode, ExerciseMode) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val options = listOf(
        RadioItem(stringResource(R.string.primary_muscl), null, ExerciseMode.MAIN),
        RadioItem(stringResource(R.string.secondary_muscl), null, ExerciseMode.SIDE)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_muscle),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.not_found_data),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary

                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                items(
                    items = list,
                    key = { it.muscleId },
                    contentType = { "muscles" }
                ) { muscle ->
                    MuscleWithRadioButton(
                        title = muscle.name,
                        checked = muscle.checked,
                        selectedValue = ExerciseMode.getMode(muscle.value),
                        onCheckBoxClick = { onToggle(muscle.muscleId, muscle.checked) },
                        options = options,
                        onRadioButtonClick = {
                            onRadioToggle(
                                muscle.muscleId,
                                ExerciseMode.getMode(muscle.value),
                                it ?: ExerciseMode.NONE
                            )
                        }
                    )
                }
            }
        }

        ModalSheetButtons(
            cancelOnClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
            cancelText = stringResource(R.string.cancel),
            doneOnClick = {
                scope.launch {
                    scaffoldState.bottomSheetState.partialExpand()
                    onClickSuccess()
                }
            },
            doneText = stringResource(R.string.add),
        )
    }
}