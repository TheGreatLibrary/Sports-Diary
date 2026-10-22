package com.sinya.projects.health.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp


/**
 * Отрисовка трех концентрических колец активности как на макете
 */
@Composable
internal fun ActivityRingsCanvas(
    kcalProgress: Float,
    exerciseProgress: Float,
    standingProgress: Float,
    orangeColor: Color = MaterialTheme.colorScheme.primary,
    darkColor: Color = MaterialTheme.colorScheme.secondary,
    greyColor: Color = MaterialTheme.colorScheme.tertiary,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceContainerLowest

    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val gap = 5.dp.toPx()
        val startAngle = -90f
        val maxSweep = 270f // Треть круга остается открытой слева внизу, как на картинке

        // 1. Внешнее кольцо (Active kcal — Оранжевое)
        val outerDiameter = size.minDimension - strokeWidth
        val outerTopLeft = Offset((size.width - outerDiameter) / 2f, (size.height - outerDiameter) / 2f)
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = maxSweep,
            useCenter = false,
            topLeft = outerTopLeft,
            size = Size(outerDiameter, outerDiameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = orangeColor,
            startAngle = startAngle,
            sweepAngle = maxSweep * kcalProgress,
            useCenter = false,
            topLeft = outerTopLeft,
            size = Size(outerDiameter, outerDiameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 2. Среднее кольцо (Exercise — Темно-серое)
        val midDiameter = outerDiameter - (strokeWidth + gap) * 2
        val midTopLeft = Offset((size.width - midDiameter) / 2f, (size.height - midDiameter) / 2f)
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = maxSweep,
            useCenter = false,
            topLeft = midTopLeft,
            size = Size(midDiameter, midDiameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = darkColor,
            startAngle = startAngle,
            sweepAngle = maxSweep * exerciseProgress,
            useCenter = false,
            topLeft = midTopLeft,
            size = Size(midDiameter, midDiameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 3. Внутреннее кольцо (Standing — Серое)
        val innerDiameter = midDiameter - (strokeWidth + gap) * 2
        val innerTopLeft = Offset((size.width - innerDiameter) / 2f, (size.height - innerDiameter) / 2f)
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = maxSweep,
            useCenter = false,
            topLeft = innerTopLeft,
            size = Size(innerDiameter, innerDiameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = greyColor,
            startAngle = startAngle,
            sweepAngle = maxSweep * standingProgress,
            useCenter = false,
            topLeft = innerTopLeft,
            size = Size(innerDiameter, innerDiameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}