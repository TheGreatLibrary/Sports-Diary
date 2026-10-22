package com.sinya.projects.ui.components.chart.utils

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.ChartPoint
import com.sinya.projects.ui.components.chart.model.SelectedPointInfo
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


object ChartLineDrawer {

    fun DrawScope.drawAxes(
        paddingPx: Float,
        axisColor: Color = Color.White
    ) {
        val chartRight = size.width - paddingPx
        val chartBottom = size.height - paddingPx

        val arrowExtension = 20f

        drawAxisWithArrow(
            start = Offset(paddingPx, chartBottom),
            end = Offset(paddingPx, paddingPx - arrowExtension),
            color = axisColor
        )

        drawAxisWithArrow(
            start = Offset(paddingPx, chartBottom),
            end = Offset(chartRight + arrowExtension, chartBottom),
            color = axisColor
        )
    }

    fun DrawScope.drawAxisWithArrow(
        start: Offset,
        end: Offset,
        color: Color = Color.White,
        strokeWidth: Float = 2f,
        arrowSize: Float = 16f,
        arrowAngle: Double = Math.PI / 6
    ) {
        drawLine(
            color = color,
            start = start,
            end = end,
            strokeWidth = strokeWidth
        )

        val angleRad = atan2(end.y - start.y, end.x - start.x).toDouble()

        val path = Path().apply {
            moveTo(end.x, end.y)

            val x1 = end.x - arrowSize * cos(angleRad - arrowAngle).toFloat()
            val y1 = end.y - arrowSize * sin(angleRad - arrowAngle).toFloat()
            lineTo(x1, y1)

            val x2 = end.x - arrowSize * cos(angleRad + arrowAngle).toFloat()
            val y2 = end.y - arrowSize * sin(angleRad + arrowAngle).toFloat()
            lineTo(x2, y2)

            close()
        }

        drawPath(
            path = path,
            color = color,
            style = Fill
        )
    }



    fun DrawScope.drawDataLines(
        lines: List<ChartLine>,
        paddingPx: Float,
        xStepPx: Float,
        yMin: Float,
        yRange: Float,
        strokeWidthPx: Float,
        dotRadiusPx: Float,
        showDots: Boolean,
        selectedPoint: SelectedPointInfo?
    ) {
        val chartBottom = size.height - paddingPx

        lines.forEach { line ->
            val validPoints = line.points.filter { !it.yValue.isNaN() }

            if (validPoints.size >= 2) {
                drawVerticalGradientDrops(
                    points = validPoints,
                    line = line,
                    lineColor = line.lineColor,
                    paddingPx = paddingPx,
                    xStepPx = xStepPx,
                    yMin = yMin,
                    yRange = yRange,
                    chartBottom = chartBottom
                )

                drawContinuousLine(
                    points = validPoints,
                    line = line,
                    lineColor = line.lineColor,
                    paddingPx = paddingPx,
                    xStepPx = xStepPx,
                    yMin = yMin,
                    yRange = yRange,
                    chartBottom = chartBottom,
                    strokeWidthPx = strokeWidthPx
                )
            }

            if (showDots) {
                validPoints.forEach { point ->
                    drawDot(
                        point = point,
                        line = line,
                        paddingPx = paddingPx,
                        xStepPx = xStepPx,
                        yMin = yMin,
                        yRange = yRange,
                        chartBottom = chartBottom,
                        dotRadiusPx = dotRadiusPx,
                        selectedPoint = selectedPoint
                    )
                }
            }
        }
    }

    private fun DrawScope.drawVerticalGradientDrops(
        points: List<ChartPoint>,
        line: ChartLine,
        lineColor: android.graphics.Color,
        paddingPx: Float,
        xStepPx: Float,
        yMin: Float,
        yRange: Float,
        chartBottom: Float
    ) {
        val baseColor = Color(lineColor.toArgb())

        points.forEach { point ->
            val index = line.points.indexOf(point)
            val x = paddingPx + index * xStepPx
            val y = yFor(point.yValue, yMin, yRange, chartBottom, paddingPx)

            // Создаем кисть, которая плавно переходит от цвета линии к прозрачному
            val fadeBrush = Brush.verticalGradient(
                colors = listOf(
                    baseColor.copy(alpha = 0.6f), // Начало: полупрозрачный цвет графика
                    Color.Transparent             // Конец: полностью прозрачный
                ),
                startY = y,
                endY = y + (chartBottom - y) * 0.7f // Растягиваем градиент не до самого низа, чтобы быстрее затухало
            )

            drawLine(
                brush = fadeBrush,
                start = Offset(x, y),
                end = Offset(x, chartBottom),
                strokeWidth = 3f // Толщина «падающей» линии
            )
        }
    }

    private fun DrawScope.drawContinuousLine(
        points: List<ChartPoint>,
        line: ChartLine,
        lineColor: android.graphics.Color,
        paddingPx: Float,
        xStepPx: Float,
        yMin: Float,
        yRange: Float,
        chartBottom: Float,
        strokeWidthPx: Float
    ) {
        if (points.size < 2) return

        val path = Path()
        val firstIndex = line.points.indexOf(points.first())
        path.moveTo(
            paddingPx + firstIndex * xStepPx,
            yFor(points.first().yValue, yMin, yRange, chartBottom, paddingPx)
        )

        for (i in 1 until points.size) {
            val index = line.points.indexOf(points[i])
            path.lineTo(
                paddingPx + index * xStepPx,
                yFor(points[i].yValue, yMin, yRange, chartBottom, paddingPx)
            )
        }

        drawIntoCanvas { canvas ->
            val paint = androidx.compose.ui.graphics.NativePaint().apply {
                color = lineColor.toArgb()
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidthPx * 1.5f // Делаем тень чуть шире основной линии
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                alpha = 150 // Делаем тень полупрозрачной
                // Магия свечения: размываем линию
                maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
            }
            // asAndroidPath() конвертирует Compose Path в Android Path
            canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
        }

        drawPath(
            path = path,
            color = Color(lineColor.toArgb()),
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    private fun DrawScope.drawDot(
        point: ChartPoint,
        line: ChartLine,
        paddingPx: Float,
        xStepPx: Float,
        yMin: Float,
        yRange: Float,
        chartBottom: Float,
        dotRadiusPx: Float,
        selectedPoint: SelectedPointInfo?
    ) {
        val index = line.points.indexOf(point)
        val x = paddingPx + index * xStepPx
        val y = yFor(point.yValue, yMin, yRange, chartBottom, paddingPx)

        val isSelected = selectedPoint?.xDate == point.xDate &&
                selectedPoint?.lineLabel == line.label

        if (isSelected) {
            drawCircle(
                color =  Color(line.lineColor.toArgb()).copy(alpha = 0.3f),
                radius = dotRadiusPx * 2.5f,
                center = Offset(x, y)
            )
        }

        drawCircle(
            color = if (isSelected) Color(line.lineColor.toArgb()) else  Color(line.lineColor.toArgb()).copy(alpha = 0.8f),
            radius = if (isSelected) dotRadiusPx * 1.5f else dotRadiusPx,
            center = Offset(x, y)
        )
    }

    private fun yFor(
        value: Float,
        yMin: Float,
        yRange: Float,
        chartBottom: Float,
        paddingPx: Float
    ): Float {
        val norm = ((value - yMin) / yRange).coerceIn(0f, 1f)
        return chartBottom - norm * (chartBottom - paddingPx)
    }
}