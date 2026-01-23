package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi

@Composable
fun CustomElementCheckBox(
    onEvent: () -> Unit,
    ex: ExerciseUi
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = ex.checked,
            onCheckedChange = { onEvent() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Text(
            text = ex.name,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis
        )
    }
}