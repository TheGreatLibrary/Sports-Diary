package com.sinya.projects.sportsdiary.ui.features.shimmer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerButton() {
    ShimmerBox(
        modifier = Modifier.fillMaxWidth(0.6f).height(38.dp),
        shape = MaterialTheme.shapes.extraLarge
    )
}