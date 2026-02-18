package com.sinya.projects.sportsdiary.presentation.exerciseEdit.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.domain.model.RadioItem

@Composable
fun <T> MuscleWithRadioButton(
    title: String,
    checked: Boolean,
    selectedValue: T,
    onCheckBoxClick: () -> Unit,
    options: List<RadioItem<T>>,
    onRadioButtonClick: (T?) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                interactionSource = interactionSource,
                onCheckedChange = { onCheckBoxClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (checked) {
            options.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 40.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        modifier = Modifier.size(30.dp),
                        selected = it.value == selectedValue,
                        onClick = { onRadioButtonClick(it.value) }
                    )
                    Text(
                        text = it.text ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}