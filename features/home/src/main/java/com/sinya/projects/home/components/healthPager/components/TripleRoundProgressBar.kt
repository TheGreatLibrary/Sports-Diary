package com.sinya.projects.home.components.healthPager.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.HealthProgressData
import com.sinya.projects.ui.components.progressBar.drawRing

@Composable
internal fun TripleRoundProgressBar(
    list: List<HealthProgressData>,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    strokeWidth: Dp = 6.dp,
    ringSpacing: Dp = 2.dp
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidthPx = strokeWidth.toPx()
        val spacingPx = ringSpacing.toPx()

        list.forEachIndexed { index, item ->
            val inset = strokeWidthPx / 2 + (spacingPx + strokeWidthPx) * index

            drawRing(backgroundColor = backgroundColor, progress = item.progress, activeColor = Color(item.color.toArgb()), inset = inset, strokeWidthPx = strokeWidthPx)
        }
    }
}