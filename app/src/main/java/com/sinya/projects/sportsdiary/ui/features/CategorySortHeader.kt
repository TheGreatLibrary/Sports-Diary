package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.FilterBuilder
import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting


@Composable
fun BoxScope.CategorySortHeader(
    modifier: Modifier,
    category: String,
    onCategoryChange: (String) -> Unit,
    isError: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onModeClick: (ModeOfSorting, Any) -> Unit,
    modesFlattened: List<Pair<FilterBuilder<Any>, ModeOfSorting>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .then(modifier)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clip(RoundedCornerShape(0.dp)),
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

        HorizontalDivider(color = MaterialTheme.colorScheme.secondaryContainer)
    }
}