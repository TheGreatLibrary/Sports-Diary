package com.sinya.projects.sportsdiary.ui.features.chart.utils

import androidx.compose.ui.geometry.Offset
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import com.sinya.projects.sportsdiary.ui.features.chart.model.SelectedPointInfo
import kotlin.math.pow
import kotlin.math.sqrt

class ChartTouchHandler {

    fun findNearestPoint(
        touchX: Float,
        touchY: Float,
        normalizedLines: List<ChartLine>,
        canvasHeight: Float,
        paddingPx: Float,
        xStepPx: Float,
        yMin: Float,
        yRange: Float,
        touchTargetSizePx: Float,
    ): SelectedPointInfo? {
        val chartBottom = canvasHeight - paddingPx

        var foundPoint: SelectedPointInfo? = null
        var minDistance = Float.MAX_VALUE

        normalizedLines.forEach { line ->
            line.points.forEachIndexed { index, point ->
                if (!point.yValue.isNaN()) {
                    val pointX = paddingPx + index * xStepPx
                    val pointY = yFor(point.yValue, yMin, yRange, chartBottom, paddingPx)

                    val distance = sqrt(
                        (touchX - pointX).pow(2) + (touchY - pointY).pow(2)
                    )

                    if (distance < touchTargetSizePx && distance < minDistance) {
                        minDistance = distance
                        foundPoint = SelectedPointInfo(
                            lineLabel = line.label,
                            xDate = point.xDate,
                            yValue = point.yValue,
                            lineColor = line.lineColor,
                            pointOffset = Offset(pointX, pointY)
                        )
                    }
                }
            }
        }

        return foundPoint
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