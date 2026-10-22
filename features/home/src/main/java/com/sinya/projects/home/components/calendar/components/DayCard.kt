package com.sinya.projects.home.components.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun RowScope.DayCard(
    modifier: Modifier = Modifier,
    title: String,
    brushBorder: Brush,
    selectionState: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onCheckClick: () -> Unit,
    isToday: Boolean,
    isCurrentMonth: Boolean,
) {
    Box(
        modifier = modifier.weight(1f),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .size(33.dp)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = when (isToday) {
                        true -> MaterialTheme.colorScheme.onPrimary
                        false -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
                .border(
                    width = 2.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    brush = brushBorder
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (selectionState) onCheckClick() else onClick()
                        },
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 15.sp,
                color = when {
                    !isCurrentMonth -> MaterialTheme.colorScheme.onSecondary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        }

        if (selectionState) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .clip(shape = MaterialTheme.shapes.extraLarge),
                contentAlignment = Alignment.TopStart
            ) {
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
                    onCheckedChange = { onCheckClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.secondaryContainer,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

        }
    }
}

