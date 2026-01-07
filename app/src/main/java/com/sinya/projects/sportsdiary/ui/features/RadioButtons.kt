package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.domain.model.RadioItem

@Composable
fun <T> RadioButtons(
    modifierItem: Modifier? = null,
    radioOptions: List<RadioItem<T>>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    shape: CornerBasedShape
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        radioOptions.forEach { item ->
            val isSelected = selectedOption == item.value

            val contentColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.onPrimary
            }

            val backgroundColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }

            RadioButton(
                modifier = modifierItem?: Modifier.weight(1f).padding(vertical = 4.dp, horizontal = 4.dp),
                text = item.text,
                painter = item.icon?.let { painterResource(it) },
                containerColor = backgroundColor,
                contentColor = contentColor,
                shape = shape,
                onClick = { item.value?.let { onOptionSelected(it) } }
            )
        }
    }
}