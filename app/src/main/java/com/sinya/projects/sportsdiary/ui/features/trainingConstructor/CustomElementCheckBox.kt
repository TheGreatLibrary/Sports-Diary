package com.sinya.projects.sportsdiary.ui.features.trainingConstructor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi

@Composable
fun CustomElementCheckBox(
    onEvent: () -> Unit,
    ex: ExerciseUi
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onEvent() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = ex.checked,
            onCheckedChange = {
                onEvent()
            }
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