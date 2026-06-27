package com.sinya.projects.sportsdiary.core.utils

import android.annotation.SuppressLint
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("DefaultLocale")
fun deltaFloat(cur: String?, prev: String?): Float? {
    val c = safeFloat(cur) ?: return null
    val p = safeFloat(prev) ?: return null
    val delta = c - p
    val absDelta = abs(delta)

    val formatted = when {
        absDelta >= 100 -> delta.roundToInt().toString()
        absDelta >= 10 -> String.format("%.1f", delta).replace(',', '.')
        absDelta >= 1 -> String.format("%.2f", delta).replace(',', '.')
        absDelta >= 0.1 -> String.format("%.3f", delta).replace(',', '.')
        else -> String.format("%.4f", delta).replace(',', '.')
    }

    return formatted.toFloat()
}