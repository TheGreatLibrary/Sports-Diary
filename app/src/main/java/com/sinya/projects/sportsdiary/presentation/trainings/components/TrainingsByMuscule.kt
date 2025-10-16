package com.sinya.projects.sportsdiary.presentation.trainings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.trainings.Training

@Composable
fun TrainingsByMuscle(
    trainings: List<Training>,
    onTrainingClick: (Int) -> Unit
) {
    val grouped = remember(trainings) { groupByMuscle(trainings) }
    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        grouped.forEach { (category, items) ->
            item {
                SectionHeader(
                    title = "$category (${items.size})",
                    expanded = expanded[category] ?: false,
                    style = MaterialTheme.typography.titleMedium,
                    rowFill = 1f,
                    onToggle = { expanded[category] = !(expanded[category] ?: false) }
                )
            }
            item {
                AnimatedVisibility(
                    visible = expanded[category] ?: false,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items.sortedByDescending { it.localDateOrNull() }.forEach { t ->
                            TrainingCard(
                                t = t,
                                rowFill = 0.95f,
                                onTrainingClick = { onTrainingClick(t.id) })
                        }
                    }
                }
            }
        }
    }
}

private fun groupByMuscle(trainings: List<Training>): Map<String, List<Training>> {
    val order = listOf("Без категории")
    val grouped = trainings.groupBy { it.category }
    val comparator = Comparator<String> { a, b ->
        val ia = order.indexOf(a).let { if (it < 0) Int.MAX_VALUE else it }
        val ib = order.indexOf(b).let { if (it < 0) Int.MAX_VALUE else it }
        if (ia != ib) ia - ib else a.compareTo(b)
    }
    return grouped.toSortedMap(comparator)
}
