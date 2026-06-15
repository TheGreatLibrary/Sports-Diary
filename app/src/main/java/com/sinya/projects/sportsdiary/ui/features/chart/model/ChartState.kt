package com.sinya.projects.sportsdiary.ui.features.chart.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ChartState(
    val yMin: Float = 0f,
    val yMax: Float = 1f,
    val yGridLines: Int = 5,
    val xStep: Dp = 80.dp,
    val contentPadding: Dp = 16.dp,
    val strokeWidth: Dp = 2.dp,
    val dotRadius: Dp = 4.dp,
    val showDots: Boolean = true,
    val showGrid: Boolean = true,
    val touchTargetSize: Dp = 20.dp
)