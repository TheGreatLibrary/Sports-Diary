package com.sinya.projects.sportsdiary.ui.features.diagram.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ChartConfig(
    val lineColor: Color = Color.Blue,
    val lineStrokeWidth: Dp = 3.dp,
    val pointColor: Color = Color.Red,
    val pointRadius: Dp = 6.dp,
    val axisColor: Color = Color.Gray,
    val textColor: Color = Color.Black,
    val textSize: Float,
    val padding: Dp = 16.dp,
    val showGrid: Boolean = true,
    val scrollEnabled: Boolean = true,
    val visibleRange: Float = 1f // Видимая часть (0-1)
)