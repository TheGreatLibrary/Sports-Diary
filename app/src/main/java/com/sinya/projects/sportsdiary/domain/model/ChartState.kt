package com.sinya.projects.sportsdiary.domain.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ChartState(
    val yMin: Float = 0f,
    val yMax: Float = 100f,
    val yGridLines: Int = 5,
    val xStep: Dp = 56.dp,
    val strokeWidth: Dp = 2.dp,
    val dotRadius: Dp = 3.5.dp,
    val contentPadding: Dp = 5.dp
)