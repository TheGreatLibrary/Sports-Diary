package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.domain.model.FilterBuilder
import com.sinya.projects.sportsdiary.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.ExerciseList
import com.sinya.projects.sportsdiary.ui.features.ModalSheetButtons
import com.sinya.projects.sportsdiary.ui.features.SearchContainer
import com.sinya.projects.sportsdiary.ui.features.SortBlock
import com.sinya.projects.sportsdiary.ui.features.rememberScrollDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySheetContent(
    category: String,
    query: String,
    isError: Boolean,
    onCategoryChange: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onClickSuccess: () -> Unit,
    filtered: List<ExerciseWithMuscles>,
    onToggle: (Int) -> Unit,
    onModeClick: (ModeOfSorting, Any) -> Unit,
    modesFlattened: List<Pair<FilterBuilder<Any>, ModeOfSorting>>,
    scaffoldState: BottomSheetScaffoldState
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val showHeader = rememberScrollDirection(listState)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.create_category),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            CustomTextField(
                value = category,
                onValueChange = onCategoryChange,
                onTrailingClick = { onCategoryChange("") },
                placeholder = stringResource(R.string.put_your_title),
                shape = MaterialTheme.shapes.extraLarge,
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                isError = isError,
                errorMessage = stringResource(R.string.category_name_is_empty)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(0.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (showHeader) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SearchContainer(
                        searchQuery = query,
                        onClear = { onQueryChange("") },
                        onValueChanged = onQueryChange
                    )
                    modesFlattened.forEach { (filter, mode) ->
                        SortBlock(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            title = stringResource(filter.titleRes),
                            radioOptions = filter.options,
                            selectedOption = filter.selectedValue,
                            onOptionSelected = { value ->
                                onModeClick(
                                    mode,
                                    filter.onSelect(value)
                                )
                            },
                            shape = filter.shape
                        )
                    }
                }

            }

            HorizontalDivider(color = MaterialTheme.colorScheme.secondaryContainer)
        }

        ExerciseList(
            listState = listState,
            filtered = filtered,
            onToggle = onToggle
        )

        ModalSheetButtons(
            cancelOnClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
            cancelText = stringResource(R.string.cancel),
            doneOnClick = {
               onClickSuccess()
            },
            doneText = stringResource(R.string.create),
        )
    }
}
