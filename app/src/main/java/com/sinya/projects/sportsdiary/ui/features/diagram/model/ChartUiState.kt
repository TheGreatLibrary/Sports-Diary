package com.sinya.projects.sportsdiary.ui.features.diagram.model

enum class Range { WEEK, MONTH, YEAR, ALL }

data class ChartUiState(
    val range: Range = Range.WEEK,
    val points: List<ChartPoint> = emptyList(),
    val yMin: Float = 0f,
    val yMax: Float = 100f
)