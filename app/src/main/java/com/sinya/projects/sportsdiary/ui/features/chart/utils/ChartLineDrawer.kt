package com.sinya.projects.sportsdiary.ui.features.chart.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartPoint
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartState
import com.sinya.projects.sportsdiary.ui.features.chart.model.SelectedPointInfo

object ChartLineDrawer {

    fun DrawScope.drawGrid(
        state: ChartState,
        yRange: Float,
        yMin: Float,
        totalPoints: Int,
        paddingPx: Float,
        xStepPx: Float,
        gridColor: Color,
        dash: PathEffect
    ) {
        val chartRight = size.width - paddingPx
        val chartBottom = size.height - paddingPx

        // Горизонтальные линии
        if (state.yGridLines > 0) {
            val step = yRange / state.yGridLines
            for (i in 0..state.yGridLines) {
                val v = yMin + i * step
                val y = chartBottom - (((v - yMin) / yRange).coerceIn(0f, 1f)) * (chartBottom - paddingPx)
                drawLine(
                    color = gridColor,
                    start = Offset(paddingPx, y),
                    end = Offset(chartRight, y),
                    strokeWidth = 1f,
                    pathEffect = dash
                )
            }
        }

        // Вертикальные линии
        for (i in 0 until totalPoints) {
            val x = paddingPx + i * xStepPx
            drawLine(
                color = gridColor,
                start = Offset(x, paddingPx),
                end = Offset(x, chartBottom),
                strokeWidth = 1f,
                pathEffect = dash
            )
        }
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

    private fun DrawScope.drawContinuousLine(
        points: List<ChartPoint>,
        line: ChartLine,
        lineColor: Color,
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

        drawPath(
            path = path,
            color = lineColor,
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
                color = line.lineColor.copy(alpha = 0.3f),
                radius = dotRadiusPx * 2.5f,
                center = Offset(x, y)
            )
        }

        drawCircle(
            color = if (isSelected) line.lineColor else line.lineColor.copy(alpha = 0.8f),
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