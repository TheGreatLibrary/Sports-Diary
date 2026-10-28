package com.sinya.projects.ui.components
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.RadioItem
import com.sinya.projects.ui.components.RadioButton
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun <T> ChartSortedRow(
    radioOptions: List<RadioItem<T>>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = LocalAppGradients.current.basedCardGradient,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        radioOptions.forEach { item ->
            val isSelected = selectedOption == item.value

            val animationColor by animateColorAsState(
                targetValue = when (!isSelected) {
                    true -> MaterialTheme.colorScheme.surfaceContainerLowest
                    else -> MaterialTheme.colorScheme.surfaceContainerLow
                }
            )
            RadioButton(
                modifier = Modifier.weight(1f).padding(vertical = 5.dp, horizontal = 7.dp),
                text = item.text,
                painter = item.icon?.let { painterResource(it) },
                containerColor = animationColor,
                contentColor =  MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { item.value?.let { onOptionSelected(it) } }
            )
        }
    }
}