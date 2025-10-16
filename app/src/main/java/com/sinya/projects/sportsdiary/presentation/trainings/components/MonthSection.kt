package com.sinya.projects.sportsdiary.presentation.trainings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Month
import java.time.format.TextStyle

@Composable
fun MonthSection(
    year: Int,
    month: Month,
    size: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        val monthTitle = month.getDisplayName(TextStyle.FULL_STANDALONE, ruLocale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(ruLocale) else it.toString() }

        SectionHeader(
            title = "$monthTitle $year (${size})",
            style = MaterialTheme.typography.titleSmall,
            rowFill = 0.95f,
            expanded = expanded,
            onToggle = onToggle
        )

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}