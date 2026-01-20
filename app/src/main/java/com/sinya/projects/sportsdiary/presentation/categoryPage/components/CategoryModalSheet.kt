package com.sinya.projects.sportsdiary.presentation.categoryPage.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.categoryPage.CategoryPageEvent
import com.sinya.projects.sportsdiary.presentation.categoryPage.CategoryPageUiState
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CustomElementCheckBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryModalSheet(
    state: CategoryPageUiState.CategoryForm,
    onEvent: (CategoryPageEvent) -> Unit,
    filtered: List<ExerciseUi>,
    content: @Composable (PaddingValues) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false,
            confirmValueChange = {
                when (it) {
                    SheetValue.Expanded -> onEvent(CategoryPageEvent.OpenBottomSheetTraining(true))
                    SheetValue.PartiallyExpanded -> onEvent(CategoryPageEvent.OpenBottomSheetTraining(false))
                    SheetValue.Hidden -> {
                        onEvent(CategoryPageEvent.OpenBottomSheetTraining(false))
                        false
                    }
                }
                true
            }
        )
    )

    LaunchedEffect(state.bottomSheetCategoryItemsState) {
        when {
            state.bottomSheetCategoryItemsState && scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded -> {
                scaffoldState.bottomSheetState.expand()
            }
            !state.bottomSheetCategoryItemsState && scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded -> {
                scaffoldState.bottomSheetState.partialExpand()
            }
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        val isExpanded = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
        if (state.bottomSheetCategoryItemsState != isExpanded) {
            onEvent(CategoryPageEvent.OpenBottomSheetTraining(isExpanded))
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        containerColor = Color.Transparent,
        sheetPeekHeight = 56.dp,
        sheetShadowElevation = 8.dp,
        sheetTonalElevation = 0.dp,
        sheetShape = MaterialTheme.shapes.large,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            val newValue = !state.bottomSheetCategoryItemsState
                            onEvent(CategoryPageEvent.OpenBottomSheetTraining(newValue))

                            if (newValue) {
                                scaffoldState.bottomSheetState.expand()
                            } else {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    }
            )
        },
        sheetContent = {
            ExerciseList(state, filtered, onEvent)
        }
    ) { padding ->
        content(padding)
    }
}

@Composable
private fun ExerciseList(
    state: CategoryPageUiState.CategoryForm,
    filtered: List<ExerciseUi>,
    onEvent: (CategoryPageEvent) -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_exercises),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        CustomTextField(
            value = state.sheetData.query,
            onValueChange = { s ->
                onEvent(CategoryPageEvent.OnQueryChange(s))
            },
            onTrailingClick = {
                onEvent(CategoryPageEvent.OnQueryChange(""))
            },
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.secondaryContainer)

        if (filtered.isEmpty()) {
            Text(
                text = stringResource(R.string.nothing_found),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.32f)
            ) {
                items(filtered) { ex ->
                    CustomElementCheckBox(
                        onEvent = {
                            onEvent(
                                CategoryPageEvent.Toggle(
                                    ex.id
                                )
                            )
                        },
                        ex = ex
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
                    onClick ={},
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
                    text = stringResource(R.string.create),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = {
                        onEvent(
                            CategoryPageEvent.AddExercise
                        )
                    }
                )
            }
        }
    }
}