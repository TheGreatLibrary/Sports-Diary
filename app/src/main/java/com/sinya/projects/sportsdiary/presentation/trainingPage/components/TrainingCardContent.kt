package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.domain.model.ExerciseItemData
import com.sinya.projects.sportsdiary.ui.features.SummaryPill
import com.sinya.projects.sportsdiary.utils.deltaFloat
import com.sinya.projects.sportsdiary.utils.deltaInt
import com.sinya.projects.sportsdiary.utils.safeFloat
import com.sinya.projects.sportsdiary.utils.trimZeros

@Composable
fun TrainingCardContent(
    modifier: Modifier = Modifier,
    id: Int,
    expanded: Boolean,
    items: List<ExerciseItemData>,
    repsUnit: String,
    weightUnit: String,
    onExpanded: (Boolean) -> Unit,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
) {
    val repsSummary = remember(items) {
        items.map { it.count }.joinToString("/") {
            it.ifBlank { "0" }
        }
    }
    val weightsSummary = remember(items) {
        items.map { it.weight }.joinToString("/") { s ->
            safeFloat(s)?.let { trimZeros(it) } ?: "0"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            .clip(shape = MaterialTheme.shapes.small)
            .animateContentSize()
            .clickable { onExpanded(!expanded) }
            .padding(8.dp)
    ) {
        CollapsedSummary(
            visible = !expanded,
            repsSummary = repsSummary,
            weightsSummary = weightsSummary,
            repsUnit = repsUnit,
            weightUnit = weightUnit,
            onClick = { onExpanded(!expanded) }
        )

        ExpandedSetsList(
            visible = expanded,
            id = id,
            items = items,
            repsUnit = repsUnit,
            weightUnit = weightUnit,
            onEditSet = onEditSet,
            onDeleteSet = onDeleteSet
        )
    }
}

@Composable
private fun CollapsedSummary(
    visible: Boolean,
    repsSummary: String,
    weightsSummary: String,
    repsUnit: String,
    weightUnit: String,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryPill(
                text = repsSummary,
                unit = repsUnit,
                modifier = Modifier.weight(1f),
            )
            SummaryPill(
                text = weightsSummary,
                unit = weightUnit,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ExpandedSetsList(
    visible: Boolean,
    id: Int,
    items: List<ExerciseItemData>,
    repsUnit: String,
    weightUnit: String,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = fadeOut() + shrinkVertically()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items) { index, rep ->
                SwipeTrainingCard(
                    rep = rep.count,
                    weight = rep.weight,
                    repsUnit = repsUnit,
                    weightUnit = weightUnit,
                    onRepChange = { new -> onEditSet(id, index, new, true) },
                    onWeightChange = { new -> onEditSet(id, index, new, false) },
                    deltaRep = deltaInt(rep.count, rep.prevCount),
                    deltaWeight = deltaFloat(rep.weight, rep.prevWeight),
                    onRemove = { onDeleteSet(id, index) },
                    onClear = {}
                )
            }
        }
    }
}