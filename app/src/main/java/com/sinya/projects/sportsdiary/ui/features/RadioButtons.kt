package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtons(
    radioOptions: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    shape: CornerBasedShape
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 10.dp)
    ) {
        radioOptions.forEachIndexed { index, it ->
            val contentColor = if (selectedOption == index) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.onPrimary
            }

            val backgroundColor = if (selectedOption == index) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }

            RadioButton(
                modifier = Modifier.weight(1f),
                text = it,
                containerColor = backgroundColor,
                contentColor = contentColor,
                shape = shape,
                onClick = { onOptionSelected(index) }
            )
        }
    }
}