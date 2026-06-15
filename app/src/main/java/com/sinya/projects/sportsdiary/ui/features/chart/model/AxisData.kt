package com.sinya.projects.sportsdiary.ui.features.chart.model

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