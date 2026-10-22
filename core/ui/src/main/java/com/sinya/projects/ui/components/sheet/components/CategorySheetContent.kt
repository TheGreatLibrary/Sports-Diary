package com.sinya.projects.ui.components.sheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.CategoryTextFieldItem
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.SmartSheetHeader
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.smartHeader.util.rememberSmartHeaderManager
import com.sinya.projects.ui.components.smartHeader.util.smartHeader
import com.sinya.projects.ui.components.sort.FilterBuilder
import com.sinya.projects.ui.components.sort.ModeOfSorting
import com.sinya.projects.ui.components.ExerciseList
import com.sinya.projects.ui.components.ModalSheetButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySheetContent(
    category: String,
    query: String,
    isError: Boolean,
    onCategoryChange: (String) -> Unit,
    onClickSuccess: () -> Unit,
    onCancelClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    filtered: List<ExerciseWithMuscles>,
    onToggle: (Int) -> Unit,
    onModeClick: (ModeOfSorting, Any) -> Unit,
    modesFlattened: List<Pair<FilterBuilder<Any>, ModeOfSorting>>,
) {
    val density = LocalDensity.current
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()
    val stateBottomSheet = remember { mutableStateOf(false) }

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
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExerciseList(
                filtered = filtered,
                onToggle = onToggle,
                listState = smartHeader.listState,
                contentPadding = PaddingValues(top = headerVisibleHeightDp + 8.dp)
            )
            ModalSheetButtons(
                cancelText = stringResource(R.string.cancel),
                doneText = stringResource(R.string.add),
                cancelOnClick = onCancelClick,
                doneOnClick = onClickSuccess
            )
        }
        SmartSheetHeader(
            modifier = Modifier.smartHeader(manager),
            onOptionSelected = { stateBottomSheet.value = !stateBottomSheet.value },
            query = query,
            title = stringResource(R.string.create_category),
            category = CategoryTextFieldItem(
                category = category,
                onCategoryChange = onCategoryChange,
                isError = isError
            ),
            onQueryChange = onQueryChange
        )


        if (stateBottomSheet.value) {
            CustomModalSheet(
                onDismiss = { stateBottomSheet.value = !stateBottomSheet.value }
            ) {
                SortedExercisesSheetContent(
                    modesFlattened = modesFlattened,
                    onOptionSelected = onModeClick
                )
            }
        }
    }
}
