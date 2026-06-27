package com.sinya.projects.sportsdiary.ui.features.shimmer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerItemCard() {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = MaterialTheme.shapes.small
    )
}