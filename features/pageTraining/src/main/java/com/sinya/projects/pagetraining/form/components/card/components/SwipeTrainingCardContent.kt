package com.sinya.projects.pagetraining.form.components.card.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.enums.TrainingMode
import com.sinya.projects.domain.model.ExerciseItem
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.HeaderOfUnitField


@Composable
internal fun SwipeTrainingCardContent(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    item: ExerciseItem,
    onMenuClick: () -> Unit,
    onPlusClick: (Int) -> Unit,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .clickable { onExpanded(!expanded) },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HeaderOfUnitField(
            title = item.title,
            onInfoClick = onMenuClick
        )
        TrainingCardContent(
            id = item.id,
            expanded = expanded,
            onExpanded = onExpanded,
            items = item.items,
            repsUnit = when (item.mode) {
                TrainingMode.COUNT -> stringResource(R.string.count)
                TrainingMode.TIME -> stringResource(R.string.second)
            },
            weightUnit = stringResource(R.string.kg),
            onEditSet = onEditSet,
            onPlusClick = onPlusClick,
            onDeleteSet = onDeleteSet,
        )
    }
}