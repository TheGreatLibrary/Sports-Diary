package com.sinya.projects.ui.components.chart.utils

import android.graphics.Color

object ChartColors {
    private var palette = listOf(
        Color.valueOf(0xFFBE4900.toInt()),
        Color.valueOf(0xFF39754C.toInt()),
        Color.valueOf(0xFF126584.toInt()),
        Color.valueOf(0xFF8DC6F8.toInt()), // Синий
        Color.valueOf(0xFFF44336.toInt()), // Красный
        Color.valueOf(0xFF5E9F5F.toInt()), // Зеленый
        Color.valueOf(0xFFFF9800.toInt()), // Оранжевый
        Color.valueOf(0xFF9C27B0.toInt()), // Фиолетовый
        Color.valueOf(0xFF00BCD4.toInt()), // Голубой
        Color.valueOf(0xFFFF5722.toInt()), // Темно-оранжевый
        Color.valueOf(0xFF96BE67.toInt()), // Лайм
        Color.valueOf(0xFFE91E63.toInt()), // Розовый
        Color.valueOf(0xFF3F51B5.toInt()), // Индиго
        Color.valueOf(0xFFFFC107.toInt()), // Янтарный
        Color.valueOf(0xFF009688.toInt()), // Бирюзовый
    )
    private var generatedCount = 14

    private fun generateDistinctColors(count: Int) {
        if (count <= generatedCount) return

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
