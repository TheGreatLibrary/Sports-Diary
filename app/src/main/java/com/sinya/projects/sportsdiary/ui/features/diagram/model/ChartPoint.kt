package com.sinya.projects.sportsdiary.ui.features.diagram.model

data class ChartPoint(
    val xLabel: String,   // "Пн", "Вт", "01.09" и т.п.
    val yValue: Float     // 0..100 или что нужно
)