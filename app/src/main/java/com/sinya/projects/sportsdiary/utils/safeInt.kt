package com.sinya.projects.sportsdiary.utils

fun safeInt(s: String?): Int? {
    val t = s?.trim().orEmpty()
    if (t.isEmpty()) return null
    val trimmed = t.trimStart('0')
    return (trimmed.ifEmpty { "0" }).toIntOrNull()
}