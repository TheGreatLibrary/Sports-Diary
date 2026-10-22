package com.sinya.projects.ui.components.chart.model

import androidx.compose.ui.graphics.Color

data class ChartLine(
    val id: Int,
    val points: List<ChartPoint>,
    val lineColor: Color,
    val label: String? = null
)