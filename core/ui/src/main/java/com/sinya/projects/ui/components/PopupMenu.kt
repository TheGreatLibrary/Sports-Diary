package com.sinya.projects.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.domain.model.PopupItem


@Composable
fun <T> BoxScope.PopupMenu(
    isMenuExpanded: Boolean = false,
    onMenuExpanded: (Boolean) -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    listItems: List<PopupItem<T>>,
    item: T
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        DropdownMenu(
            expanded = isMenuExpanded,
            shape = MaterialTheme.shapes.small,
            containerColor = color,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            onDismissRequest = { onMenuExpanded(!isMenuExpanded) }
        ) {
            listItems.forEach {
                DropdownMenuItem(
                    modifier = Modifier.height(35.dp),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            it.icon?.let {
                                Icon(
                                    painter = painterResource(it),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    onClick = {
                        it.onClick(item)
                        onMenuExpanded(!isMenuExpanded)
                    }
                )
            }
        }
    }
}