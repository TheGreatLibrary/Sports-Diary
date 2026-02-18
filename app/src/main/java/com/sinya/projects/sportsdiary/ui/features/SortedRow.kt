package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.domain.model.RadioItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> SortedRow(
    modifier: Modifier?,
    title: String,
    selectedOption: T?,
    radioOptions: List<RadioItem<T>>,
    onOptionSelected: (T) -> Unit,
    shape: Shape,
    selectedContentColor: Color = MaterialTheme.colorScheme.primaryContainer,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    selectedContainerColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxRowHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        radioOptions.forEach { item ->
            val isSelected = selectedOption == item.value

            val contentColor = if (isSelected) selectedContentColor
                                        else unselectedContentColor

            val backgroundColor = if (isSelected) selectedContainerColor
                                            else unselectedContainerColor

            RadioButton(
                modifier = modifier ?: Modifier
                    .weight(1f)
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