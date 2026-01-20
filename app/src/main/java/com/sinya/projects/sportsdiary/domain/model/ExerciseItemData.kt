package com.sinya.projects.sportsdiary.domain.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.sinya.projects.sportsdiary.utils.deltaInt

data class ExerciseItemData(
    val exerciseId: Int,
    val index: Int,
    val count: String,
    val weight: String,
    val prevCount: String? = null,
    val prevWeight: String? = null
)

@Composable
fun List<ExerciseItemData>.toColoringRepsList(): List<Pair<String, Color>> {
    return map {
        val delta = (deltaInt(it.count, it.prevCount) ?: 0)
        val currentColor = if (delta > 0) MaterialTheme.colorScheme.primary
                                else if (delta < 0) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onPrimary

        Pair(
            first = it.count.ifBlank { "0" },
            second = currentColor
        )
    }
}

@Composable
fun List<ExerciseItemData>.toColoringWeightList(): List<Pair<String, Color>> {
    return map {
        val delta = (deltaInt(it.weight, it.prevWeight) ?: 0)
        val currentColor = if (delta > 0) MaterialTheme.colorScheme.primary
        else if (delta < 0) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.onPrimary

        Pair(
            first = it.weight.ifBlank { "0" },
            second = currentColor
        )
    }
}

@Composable
fun List<Pair<String, Color>>.coloringText(): AnnotatedString {
    return buildAnnotatedString {
        forEachIndexed { index, it ->
            withStyle(SpanStyle(color = it.second)) { append(it.first) }
            if (index != size - 1) withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) { append("/") }
        }
    }
}
