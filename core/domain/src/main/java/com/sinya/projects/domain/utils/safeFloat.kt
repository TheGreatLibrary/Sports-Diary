package com.sinya.projects.domain.utils

fun safeFloat(s: String?): Float? {
    if (s.isNullOrBlank()) return null
    val norm = s.replace(',', '.').trim()
    val cleaned = if (norm.endsWith(".")) norm.dropLast(1) else norm
    if (cleaned.isBlank()) return null
    return cleaned.toFloatOrNull()
}