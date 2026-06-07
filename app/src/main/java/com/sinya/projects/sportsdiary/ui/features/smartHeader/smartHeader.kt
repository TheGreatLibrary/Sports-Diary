package com.sinya.projects.sportsdiary.ui.features.smartHeader

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged

@Stable
fun Modifier.smartHeader(
    manager: SmartHeaderManager
): Modifier = this
    .onSizeChanged { size ->
        manager.setHeight(size.height.toFloat())
    }
    .graphicsLayer {
        val offset = manager.headerOffsetPx.value
        val height = manager.headerHeightPx.value

        translationY = offset

        if (height > 0f) {
            val progress = (-offset / height).coerceIn(0f, 1f)
            alpha = 1f - progress

        }
    }