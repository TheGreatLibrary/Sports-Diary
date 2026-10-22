package com.sinya.projects.ui.components.sheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.sort.FilterBuilder
import com.sinya.projects.ui.components.sort.ModeOfSorting
import com.sinya.projects.ui.components.sort.SortParam
import com.sinya.projects.ui.components.SortedRow
import kotlin.collections.forEach


@Composable
fun SortSheetContent(
    selectedMode: ModeOfSorting,
    onOptionSelected: ((Int?) -> Unit)?,
    onCategoryOptionSelected: (SortParam) -> Unit,
    categories: List<FilterBuilder<Any>>,
) {
    Column(
        modifier = Modifier.padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.sort_title),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        onOptionSelected?.let {
            SortedRow(
                modifier = Modifier.padding(4.dp),
                title = stringResource(R.string.sorted_by),
                radioOptions = ModeOfSorting.trainingsModes(),
                selectedOption = selectedMode.code,
                onOptionSelected = onOptionSelected,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }

        categories.forEach { filter ->
            SortedRow(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                title = stringResource(filter.titleRes),
                radioOptions = filter.options,
                selectedOption = filter.selectedValue,
                onOptionSelected = { onCategoryOptionSelected(filter.onSelect(it)) },
                shape = filter.shape
            )
        }
    }
}