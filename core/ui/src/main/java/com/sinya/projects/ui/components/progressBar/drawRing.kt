package com.sinya.projects.ui.components.progressBar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawRing(
    backgroundColor: Color,
    activeColor: Color,
    progress: Float,
    inset: Float,
    strokeWidthPx: Float
) {
    val ringSize = size.width - (inset * 2)
    val topLeftOffset = Offset(inset, inset+15)
    val sweepAngle = 360f * progress

    drawArc(
        color = backgroundColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeftOffset,
        size = Size(ringSize, ringSize),
        style = Stroke(width = strokeWidthPx)
    )

    drawArc(
        color = activeColor,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeftOffset,
        size = Size(ringSize, ringSize),
        style = Stroke(
            width = strokeWidthPx,
            cap = StrokeCap.Round
        )
    )
}