package com.sinya.projects.pagetraining.form.components.card.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sinya.projects.domain.model.ExerciseItemData
import com.sinya.projects.domain.utils.deltaInt

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

