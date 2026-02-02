package com.sinya.projects.sportsdiary.presentation.categoryPage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.model.ExerciseUi
import com.sinya.projects.sportsdiary.ui.features.ExerciseList
import com.sinya.projects.sportsdiary.ui.features.ModalSheetButtons
import com.sinya.projects.sportsdiary.ui.features.SearchContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSheetContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onClickSuccess: () -> Unit,
    filtered: List<ExerciseUi>,
    onToggle: (Int) -> Unit,
    scaffoldState: BottomSheetScaffoldState
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_exercises),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        SearchContainer(
            searchQuery = query,
            onClear = { onQueryChange("") },
            onValueChanged = onQueryChange
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.secondaryContainer)

        ExerciseList(
            filtered = filtered,
            onToggle = onToggle
        )

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