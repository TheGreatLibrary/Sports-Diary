package com.sinya.projects.ui.components.chart.utils

import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.components.chart.model.ChartState

fun ChartState.calculateChartState(lines: List<com.sinya.projects.domain.model.ChartLine>): ChartState {
    val allYValues = lines.flatMap { line ->
        line.points.map { it.yValue }.filter { !it.isNaN() }
    }
    val maxYValue = allYValues.maxOrNull() ?: 0f
    val scale = ChartDataUtils.buildIntScale(maxYValue)

    return this.copy(
        yMin = 0f,
        yMax = scale.max.toFloat(),
        yGridLines = scale.lines,
        xStep = 80.dp
    )
}