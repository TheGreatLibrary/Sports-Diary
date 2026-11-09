package com.sinya.projects.ui.components.sheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.components.RoundedIcon

@Composable
fun BoxScope.SheetButtons(
    list: List<Pair<Painter, () -> Unit>>
) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        list.forEach {
            RoundedIcon(
                modifier = Modifier,
                enabled = true,
                onClick = it.second,
                description = null,
                painter = it.first,
                size = 50.dp,
                isSelected = true,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unselectedContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}