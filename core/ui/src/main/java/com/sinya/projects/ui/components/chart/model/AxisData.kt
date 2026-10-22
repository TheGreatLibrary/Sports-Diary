package com.sinya.projects.ui.components.chart.model

import com.sinya.projects.domain.model.ChartLine

data class AxisData(
    val unifiedXAxis: List<String>,
    val mainUnifiedXAxis: List<Pair<String, Int>>? = null,
    val xDateToIndex: Map<String, Int>,
    val normalizedLines: List<ChartLine>,
    val yRange: Float,
    val yMin: Float,
    val yLabels: List<String>,
    val yMax: Float
)