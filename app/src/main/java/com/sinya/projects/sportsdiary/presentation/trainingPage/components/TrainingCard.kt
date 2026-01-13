package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.model.ExerciseItem
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.ui.features.HeaderInfo

@Composable
fun TrainingCard(
    it: ExerciseItem,
    onInfoClick: (Int) -> Unit,
    onPlusClick: (Int) -> Unit,
    onMinusClick: (Int) -> Unit,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { expanded = !expanded },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderCard(
            item = it,
            onInfoClick = onInfoClick,
            onPlusClick = onPlusClick,
            onMinusClick = onMinusClick
        )
        TrainingCardContent(
            id = it.id,
            expanded = expanded,
            onExpanded = { expanded = it },
            items = it.item,
            repsUnit = stringResource(R.string.count),
            weightUnit = stringResource(R.string.kg),
            onEditSet = onEditSet,
            onDeleteSet = onDeleteSet,
        )
    }
}

@Composable
private fun HeaderCard(
    item: ExerciseItem,
    onInfoClick: (Int) -> Unit,
    onPlusClick: (Int) -> Unit,
    onMinusClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderInfo(
            title = item.title,
            onInfoClick = { onInfoClick(item.id) }
        )
        HeaderActiveButton(
            id = item.id,
            onPlusClick = onPlusClick,
            onMinusClick = onMinusClick
        )
    }
}

@Composable
private fun HeaderActiveButton(
    id: Int,
    onPlusClick: (Int) -> Unit,
    onMinusClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimationIcon(
            onClick = { onPlusClick(id) },
            icon = painterResource(R.drawable.ic_plus),
            description = "Delete",
            isSelected = true,
            size = 25.dp,
            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        AnimationIcon(
            onClick = { onMinusClick(id) },
            icon = painterResource(R.drawable.ic_minus),
            description = "Delete",
            isSelected = true,
            size = 25.dp,
            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}