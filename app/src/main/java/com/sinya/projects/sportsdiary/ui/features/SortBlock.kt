package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.domain.model.RadioItem

@Composable
fun <T> SortBlock(
    modifier: Modifier?,
    title: String,
    selectedOption: T?,
    radioOptions: List<RadioItem<T>>,
    onOptionSelected: (T) -> Unit,
    shape: Shape = MaterialTheme.shapes.extraLarge
) {

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        items(
            items = radioOptions
        ) { item ->
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
                modifier = modifier ?: Modifier
                    .padding(vertical = 4.dp, horizontal = 4.dp),
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