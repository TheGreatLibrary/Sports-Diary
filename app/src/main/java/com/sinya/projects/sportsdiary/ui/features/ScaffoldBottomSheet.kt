package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.model.CategoryEntity
import com.sinya.projects.sportsdiary.domain.model.CategorySheetItem
import com.sinya.projects.sportsdiary.presentation.categoryPage.CategoryPageUiState
import com.sinya.projects.sportsdiary.presentation.categoryPage.components.CategorySheetContent
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.ui.theme.SportsDiaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldBottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    sheetContent: @Composable (ColumnScope.() -> Unit),
    content: @Composable (PaddingValues) -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 56.dp,
        sheetShadowElevation = 8.dp,
        sheetTonalElevation = 0.dp,
        sheetShape = MaterialTheme.shapes.large,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = sheetContent
    ) { padding ->
        content(padding)
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryModalSheetPreview() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded
        ),
    )

    SportsDiaryTheme(true, dynamicColor = false) {
        ScaffoldBottomSheet(

            scaffoldState = scaffoldState,
            sheetContent = {
                CategorySheetContent(
                    state = CategoryPageUiState.CategoryForm(
                        item = CategoryEntity(
                            category = TypeTraining(1, "d"),
                            items = listOf()
                        ),
                        sheetData = CategorySheetItem(
                            query = "",
                            items = emptyList()
                        ),
                    ),
                    onEvent = {},
                    filtered = listOf(
                        ExerciseUi(
                            1,
                            "Push up Push up Push up Push up Push upPush upPush up Push up Push up Push upPush up",
                            false
                        ),
                        ExerciseUi(2, "Push down", false),
                        ExerciseUi(3, "Push left", true),
                        ExerciseUi(4, "Push up", false),
                        ExerciseUi(5, "Push down", false),
                        ExerciseUi(6, "Push left", true),
                        ExerciseUi(7, "Push up", false),
                        ExerciseUi(8, "Push down", false),
                        ExerciseUi(9, "Push left", true),
                        ExerciseUi(10, "Push up", false),
                        ExerciseUi(11, "Push down", false),
                        ExerciseUi(12, "Push left", true),
                    ),
                    scaffoldState = scaffoldState
                )
            },
            content = {}
        )
    }
}