package com.sinya.projects.sportsdiary.ui.features.chart.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class SelectedPointInfo(
    val lineLabel: String?,
    val xDate: String,
    val yValue: Float,
    val lineColor: Color,
    val pointOffset: Offset
)