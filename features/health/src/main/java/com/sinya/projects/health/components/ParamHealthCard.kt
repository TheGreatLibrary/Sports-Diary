package com.sinya.projects.health.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
internal fun ParamHealthCard(
    modifier: Modifier,
    gradient: Brush,
    verticalArr: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    body: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                shape = MaterialTheme.shapes.small,
                brush = gradient
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = verticalArr
    ) {
        body()
    }
}