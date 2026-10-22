package com.sinya.projects.pagetraining.form.components.card.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sinya.projects.domain.model.ExerciseItemData
import com.sinya.projects.domain.utils.deltaFloat

@Composable
fun List<ExerciseItemData>.toColoringWeightList(): List<Pair<String, Color>> {
    return map {
        val delta = (deltaFloat(it.weight, it.prevWeight)
            ?: 0f)
        val currentColor = if (delta > 0f) MaterialTheme.colorScheme.primary
        else if (delta < 0f) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.onPrimary

        Pair(
            first = it.weight.ifBlank { "0" },
            second = currentColor
        )
    }
}

