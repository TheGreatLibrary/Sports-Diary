package com.sinya.projects.ui.components.shimmer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun ShimmerBox(
    modifier: Modifier,
    shape: CornerBasedShape,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmerLoading()
    )
}