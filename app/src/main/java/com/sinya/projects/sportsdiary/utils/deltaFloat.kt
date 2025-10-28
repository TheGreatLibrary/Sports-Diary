package com.sinya.projects.sportsdiary.utils

import kotlin.math.roundToInt

fun deltaFloat(cur: String?, prev: String?): Int? {
    val c = safeFloat(cur) ?: return null
    val p = safeFloat(prev) ?: return null
    return (c - p).roundToInt()
}