package com.sinya.projects.domain.model

import android.graphics.Color


data class ChartLine(
    val id: Int,
    val points: List<ChartPoint>,
    val lineColor: Color,
    val label: String? = null
)