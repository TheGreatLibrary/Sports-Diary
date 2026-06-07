package com.sinya.projects.sportsdiary.presentation.trainings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.core.domain.model.FilterBuilder
import com.sinya.projects.sportsdiary.core.domain.model.ModeOfSorting
import com.sinya.projects.sportsdiary.core.domain.model.SortParam
import com.sinya.projects.sportsdiary.navigation.NavigationTopBar
import com.sinya.projects.sportsdiary.ui.features.SortedRow
import kotlin.collections.forEach

@Composable
fun BoxScope.TrainingsHeader(
    modifier: Modifier,
    onOptionSelected: (SortParam) -> Unit,
    onModeChange: (Int?) -> Unit,
    selectedMode: ModeOfSorting,
    categories: List<FilterBuilder<Any>>,
    navigationType: TypeAppTopNavigation
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .then(modifier)
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(0.dp)),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NavigationTopBar(navigationType)

        SortedRow(
            modifier = Modifier.padding(4.dp),
            title = stringResource(R.string.sorted_by),
            radioOptions = ModeOfSorting.trainingsModes(),
            selectedOption = selectedMode.code,
            onOptionSelected = { onModeChange(it) },
            shape = MaterialTheme.shapes.extraSmall,
        )

        categories.forEach { filter ->
            SortedRow(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                title = stringResource(filter.titleRes),
                radioOptions = filter.options,
                selectedOption = filter.selectedValue,
                onOptionSelected = { value -> onOptionSelected(filter.onSelect(value)) },
                shape = filter.shape
            )
        }
    }
}

