package com.sinya.projects.sportsdiary.presentation.exercises.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.presentation.exercises.ExercisesEvent
import com.sinya.projects.sportsdiary.ui.features.ModalSheetButtons
import com.sinya.projects.sportsdiary.ui.features.SortedRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesSheetContent(
    list: List<ModeOfSorting>,
    exercises: List<ExerciseWithMuscles>,
    onEvent: (ExercisesEvent) -> Unit,
    scaffoldState: BottomSheetScaffoldState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val modesFlattened = remember(exercises, list) {
        list.flatMap { mode ->
            mode.categories<Any, Any>(exercises, context).map { filter ->
                filter to mode
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.sort_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            items(items = modesFlattened) { (filter, mode) ->
                SortedRow(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    title = stringResource(filter.titleRes),
                    radioOptions = filter.options,
                    selectedOption = filter.selectedValue,
                    onOptionSelected = { value ->
                        onEvent(ExercisesEvent.SortParamChange(mode, filter.onSelect(value)))
                    },
                    shape = filter.shape
                )
            }

            item {
                Spacer(Modifier.height(20.dp))
            }

        }

        ModalSheetButtons(
            cancelOnClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
            cancelText = stringResource(R.string.cancel),
            doneOnClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
            doneText = stringResource(R.string.complete),
        )
    }
}