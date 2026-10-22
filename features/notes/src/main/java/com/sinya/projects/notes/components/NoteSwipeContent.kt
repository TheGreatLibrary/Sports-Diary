package com.sinya.projects.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.RoundIconButton
import com.sinya.projects.ui.components.textField.CustomTextField
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
internal fun NoteSwipeContent(
    modifier: Modifier,
    title: String,
    selected: Boolean,
    maxLines: Int = Int.MAX_VALUE,
    onSelected: () -> Unit,
    onMenuClick: () -> Unit,
    selectionState: Boolean,
    onValueChange: (String) -> Unit,
    edit: Boolean,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (selectionState) {
            Checkbox(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .clip(shape = MaterialTheme.shapes.extraLarge)
                    .size(15.dp)
                    .scale(0.8f),
                checked = selected,
                onCheckedChange = { onSelected() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    brush = LocalAppGradients.current.basedCardGradient,
                    shape = MaterialTheme.shapes.small
                )
                .clip(  shape = MaterialTheme.shapes.small)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoundIconButton(
                modifier = Modifier,
                buttonSize = 25,
                iconSize = 20,
                icon = R.drawable.ic_menu,
                onClick = onMenuClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!edit) {
                    Text(
                        text = title,
                        maxLines = maxLines,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else {
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                shape = MaterialTheme.shapes.small
                            ),
                        value = title,
                        onValueChange = onValueChange,
                        placeholder = stringResource(R.string.note_of_morning_exercises),
                        maxLines = 10,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}