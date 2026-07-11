package com.sinya.projects.sportsdiary.ui.features.map.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Blue700
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Green700


fun getMuscleGradientColors(level: Int, value: Float): Pair<Color, Color> {
    if (level == 0) return Color(0xFF553FB5) to Color(0xFF44309F)

    val adjustedValue = when (level) {
        1 -> value * 0.6f
        else -> value
    }

    val centerColor = when {
        adjustedValue <= 0.2f -> lerp(
            Color(0xFF3A48CE),
            Blue700,
            adjustedValue / 0.2f
        )  // Фиолетовый -> Голубой
        adjustedValue <= 0.4f -> lerp(
            Blue700,
            Green700,
            (adjustedValue - 0.2f) / 0.2f
        ) // Голубой -> Зеленый
        adjustedValue <= 0.6f -> lerp(
            Green700,
            Color(0xFFFFD54F),
            (adjustedValue - 0.4f) / 0.2f
        ) // Зеленый -> Желтый
        adjustedValue <= 0.8f -> lerp(
            Color(0xFFFFD54F),
            Color(0xFFFF9800),
            (adjustedValue - 0.6f) / 0.2f
        ) // Желтый -> Оранжевый
        else -> lerp(
            Color(0xFFFF9800),
            Color(0xFFE53935),
            (adjustedValue - 0.8f) / 0.2f
        ) // Оранжевый -> Красный
    }

    // Глубокие оттенки краев для создания 3D-объема
    val edgeColor = when {
        adjustedValue <= 0.2f -> Color(0xFF44309F)
        adjustedValue <= 0.4f -> Color(0xFF1B5E20)
        adjustedValue <= 0.6f -> Color(0xFFF57F17) // Янтарный/Темно-желтый
        adjustedValue <= 0.8f -> Color(0xFFE65100)
        else -> Color(0xFFB71C1C)
    }

    return centerColor to edgeColor
}


