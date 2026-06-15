package com.sinya.projects.sportsdiary.ui.features.chart.utils

import androidx.compose.ui.graphics.Color
import kotlin.math.abs


fun hsv(hue: Float, saturation: Float, value: Float): Color {
    val h = hue.coerceIn(0f, 1f)
    val s = saturation.coerceIn(0f, 1f)
    val v = value.coerceIn(0f, 1f)

    val c = v * s
    val x = c * (1 - abs((h * 6) % 2 - 1))
    val m = v - c

    val (r, g, b) = when ((h * 6).toInt() % 6) {
        0 -> Triple(c, x, 0f)
        1 -> Triple(x, c, 0f)
        2 -> Triple(0f, c, x)
        3 -> Triple(0f, x, c)
        4 -> Triple(x, 0f, c)
        5 -> Triple(c, 0f, x)
        else -> Triple(0f, 0f, 0f)
    }

    return Color(r + m, g + m, b + m, 1f)
}
