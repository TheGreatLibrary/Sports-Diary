package com.sinya.projects.sportsdiary.ui.features.chart.utils

import androidx.compose.ui.graphics.Color

object ChartColors {
    private var palette = listOf(
        Color(0xFFBE4900),
        Color(0xFF39754C),
        Color(0xFF126584),
        Color(0xFF8DC6F8), // Синий
        Color(0xFFF44336), // Красный
        Color(0xFF5E9F5F), // Зеленый
        Color(0xFFFF9800), // Оранжевый
        Color(0xFF9C27B0), // Фиолетовый
        Color(0xFF00BCD4), // Голубой
        Color(0xFFFF5722), // Темно-оранжевый
        Color(0xFF96BE67), // Лайм
        Color(0xFFE91E63), // Розовый
        Color(0xFF3F51B5), // Индиго
        Color(0xFFFFC107), // Янтарный
        Color(0xFF009688), // Бирюзовый
    )
    private var generatedCount = 14

    private fun generateDistinctColors(count: Int) {
        if (count <= generatedCount) return // Уже сгенерировано достаточно

        val goldenRatio = 0.618033988749895
        palette = (0 until count).map { i ->
            val hue = (i * goldenRatio) % 1.0
            hsv(hue.toFloat(), 0.7f, 0.9f)
        }
        generatedCount = count
    }

    fun getColor(index: Int, totalCount: Int): Color {
        if (palette.size < totalCount) {
            generateDistinctColors(totalCount)
        }
        return palette[index % palette.size]
    }
}
