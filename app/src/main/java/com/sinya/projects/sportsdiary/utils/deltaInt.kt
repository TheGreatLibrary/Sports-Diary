package com.sinya.projects.sportsdiary.utils

fun deltaInt(cur: String?, prev: String?): Int? {
    val c = safeInt(cur) ?: return null
    val p = safeInt(prev) ?: return null
    return c - p
}
