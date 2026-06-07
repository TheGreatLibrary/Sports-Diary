package com.sinya.projects.sportsdiary.presentation.exercises.components

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
import com.sinya.projects.sportsdiary.core.domain.model.RadioItem
import com.sinya.projects.sportsdiary.navigation.NavigationTopBar
import com.sinya.projects.sportsdiary.ui.features.SearchContainer
import com.sinya.projects.sportsdiary.ui.features.SortedRow

@Composable
fun BoxScope.ExercisesHeader(
    modifier: Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onOptionSelected: (Int) -> Unit,
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
            title = stringResource(R.string.sort_title),
            radioOptions = listOf(RadioItem(null, R.drawable.icon_sort, 0)),
            selectedOption = 0,
            onOptionSelected = onOptionSelected,
            shape = MaterialTheme.shapes.extraSmall,
        )

        SearchContainer(
            searchQuery = query,
            onClear = { onQueryChange("") },
            onValueChanged = onQueryChange,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}