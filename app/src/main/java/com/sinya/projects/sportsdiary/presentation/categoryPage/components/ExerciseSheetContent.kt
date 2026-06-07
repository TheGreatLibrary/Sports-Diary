package com.sinya.projects.sportsdiary.presentation.categoryPage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.model.FilterBuilder
import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.ui.features.ExerciseList
import com.sinya.projects.sportsdiary.ui.features.ExerciseSortHeader
import com.sinya.projects.sportsdiary.ui.features.ModalSheetButtons
import com.sinya.projects.sportsdiary.ui.features.smartHeader.rememberSmartHeaderManager
import com.sinya.projects.sportsdiary.ui.features.smartHeader.smartHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSheetContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onClickSuccess: () -> Unit,
    filtered: List<ExerciseWithMuscles>,
    onToggle: (Int) -> Unit,
    onModeClick: (ModeOfSorting, Any) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    modesFlattened: List<Pair<FilterBuilder<Any>, ModeOfSorting>>
) {
    val scope = rememberCoroutineScope()
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()
    val density = LocalDensity.current

    val headerVisibleHeightDp = with(density) {
        (smartHeader.headerHeightPx + smartHeader.headerOffsetPx)
            .coerceAtLeast(0f)
            .toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(smartHeader.headerScrollConnection),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
        ) {

            ExerciseList(
                filtered = filtered,
                onToggle = onToggle,
                listState = smartHeader.listState,
                contentPadding = PaddingValues(top = headerVisibleHeightDp + 8.dp),
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

        ExerciseSortHeader(
            modifier = Modifier.smartHeader(manager),
            query = query,
            onQueryChange = onQueryChange,
            onModeClick = onModeClick,
            modesFlattened = modesFlattened
        )
    }
}