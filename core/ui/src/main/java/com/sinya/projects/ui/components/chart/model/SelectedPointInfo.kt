package com.sinya.projects.ui.components.chart.model

import androidx.compose.ui.geometry.Offset

data class SelectedPointInfo(
    val lineLabel: String?,
    val xDate: String,
    val yValue: Float,
    val lineColor: android.graphics.Color,
    val pointOffset: Offset
)