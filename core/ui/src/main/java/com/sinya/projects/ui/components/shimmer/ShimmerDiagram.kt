package com.sinya.projects.ui.components.shimmer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerDiagram() {
    ShimmerBox(
        Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = MaterialTheme.shapes.small
    )
}