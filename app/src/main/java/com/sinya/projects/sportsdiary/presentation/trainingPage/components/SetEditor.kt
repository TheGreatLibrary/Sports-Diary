package com.sinya.projects.sportsdiary.presentation.trainingPage.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.SetRow
import kotlin.math.roundToInt


@Composable
fun SetsEditor(
    id: Int,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    reps: List<String>,
    weights: List<String>,
    repsUnit: String = "шт",
    weightUnit: String = "кг",
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
) {
    val repsSummary = remember(reps) { reps.joinToString("/") { it.ifBlank { "0" } } }
    val weightsSummary = remember(weights) { weights.joinToString("/") { s ->
            safeFloat(s)?.let { trimZeros(it) } ?: "0"
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onExpanded(!expanded) },
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            AnimatedVisibility(
                visible = !expanded,
                enter = expandVertically() + fadeIn(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExpanded(!expanded) },
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

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = true
                ) {
                    itemsIndexed(reps) { index, rep ->
                        val weight = weights.getOrNull(index) ?: "0f"
                        SetRow(
                            rep = rep,
                            weight = weight,
                            repsUnit = repsUnit,
                            weightUnit = weightUnit,
                            onRepChange = { new ->
                                onEditSet(id, index, new, true)
                            },
                            onWeightChange = { new ->
                                onEditSet(id, index, new, false)
                            },
                            deltaRep = deltaInt(rep, reps.getOrNull(index - 1)),
                            deltaWeight = deltaFloat(weight, weights.getOrNull(index - 1)),
                            onRemove = { onDeleteSet(id, index) }
                        )
                    }
                }
            }
        }
    }
}


private fun safeInt(s: String?): Int? {
    val t = s?.trim().orEmpty()
    if (t.isEmpty()) return null
    // срезаем ведущие нули, но оставляем одиночный "0"
    val trimmed = t.trimStart('0')
    return (if (trimmed.isEmpty()) "0" else trimmed).toIntOrNull()
}

private fun safeFloat(s: String?): Float? {
    if (s.isNullOrBlank()) return null
    val norm = s.replace(',', '.').trim()
    // поддержим "12." → "12"
    val cleaned = if (norm.endsWith(".")) norm.dropLast(1) else norm
    if (cleaned.isBlank()) return null
    return cleaned.toFloatOrNull()
}

private fun deltaInt(cur: String?, prev: String?): Int? {
    val c = safeInt(cur) ?: return null
    val p = safeInt(prev) ?: return null
    return c - p
}

fun deltaFloat(cur: String?, prev: String?): Int? {
    val c = safeFloat(cur) ?: return null
    val p = safeFloat(prev) ?: return null
    // округляем до целого для компактного бейджа
    return (c - p).roundToInt()
}

private fun trimZeros(f: Float): String = if (f % 1f == 0f) f.toInt().toString() else f.toString()