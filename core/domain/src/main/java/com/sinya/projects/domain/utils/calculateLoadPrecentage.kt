package com.sinya.projects.domain.utils

fun calculateLoadPercentage(currentTonnage: Float, previousTonnage: Float): Float {
    if (currentTonnage == 0f) return 0.0f
    if (previousTonnage == 0f) return 1.0f
    val ratio = currentTonnage / previousTonnage
    return ratio.coerceIn(0.0f, 1.0f)
}