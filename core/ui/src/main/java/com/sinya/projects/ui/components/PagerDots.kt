package com.sinya.projects.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PagerDots(
    currentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    count: Int,
    currentPage: Int,
    size: Dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) {
            val color =
                if (it == currentPage) currentColor
                else MaterialTheme.colorScheme.onSecondary

            Box(
                modifier = Modifier
                    .size(size)
                    .background(color, shape = MaterialTheme.shapes.extraLarge)
            )
        }
    }
}